/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.factory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.utils.EmfWsdlUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;

/**
 * 
 * This class is used to store the nodes created, along with their model
 * elements. So that we can get a node from a given model element. It is
 * populated the first time a node is created, and removed when a node is
 * deleted.
 */
public class TreeNodeMapper {

    private Map<IModelObject, List<WeakReference<ITreeNode>>> nodeMap = new HashMap<IModelObject, List<WeakReference<ITreeNode>>>();

    /*
     * Stores the model object and a map with category and tree node
     * corresponding to that Used in cases of datatype nodes, which have
     * built-in, inline and project types categories Also used for Input, Output
     * and Fault categories in the left tree
     */
    private Map<Object, Map<String, ITreeNode>> categoryNodeMap = new HashMap<Object, Map<String, ITreeNode>>();

    /**
     * Returns the first tree node which is not CATEGORY_REFERENCE. If there is
     * no such node, then return the first CATEGORY_REFERENCE node for the given
     * model element. Because a model object can has more than one tree node
     * this method is deprecated.
     * 
     * @deprecated use {@link #getTreeNode(IModelObject, Collection, ITreeNode)}
     * @param modelObject
     * @return the first tree node mapped to the model object or null if no such
     *         is found
     */
    public ITreeNode getTreeNode(final IModelObject modelObject) {
        ExcludeCategoryMatcher excludeCategories = new ExcludeCategoryMatcher(ITreeNode.CATEGORY_REFERENCE);
        List<ITreeNode> foundTreeNodes = getTreeNode(modelObject, excludeCategories);
        if (!foundTreeNodes.isEmpty()) {
            return foundTreeNodes.get(0);
        }

        List<WeakReference<ITreeNode>> treeNodes = nodeMap.get(modelObject);
        ITreeNode firstTreeNode = null;
        if (treeNodes != null && !treeNodes.isEmpty()) {
            Iterator<WeakReference<ITreeNode>> treeNodesIterator = treeNodes.iterator();
            while (firstTreeNode == null && treeNodesIterator.hasNext()) {
                WeakReference<ITreeNode> weakTreeNode = treeNodesIterator.next();
                firstTreeNode = weakTreeNode.get();
                if (firstTreeNode == null) {
                    treeNodesIterator.remove();
                }
            }
        }
        return firstTreeNode;
    }

    /**
     * Get tree nodes that corresponds to the modelObject, its parent tree node
     * and all categories from categories list.
     * 
     * @param modelObject
     *            for which tree nodes map will be searched for
     * @param categories
     *            categories which the returned tree nodes must have
     * @param parentNode
     *            in case that is null, children with null parent will be
     *            searched for categories
     * @return never returns null
     */
    public List<ITreeNode> getTreeNode(final IModelObject modelObject, int categories, ITreeNode parentNode) {
        ParentCategoryMatcher matcher = new ParentCategoryMatcher(categories, parentNode);

        return getTreeNode(modelObject, matcher);
    }

    /**
     * Gets all the tree nodes that match at least one of the given categories
     * 
     * @param modelObject
     * @param categories
     * @return
     */
    public List<ITreeNode> getTreeNode(final IModelObject modelObject, int categories) {
        CategoryMatcher matcher = new CategoryMatcher(categories);

        return getTreeNode(modelObject, matcher);
    }

    /**
     * Get tree nodes that corresponds to the modelObject, its parent tree node
     * and all categories of the parent node.
     * 
     * @param parentNode
     *            must not be null, because its categories will be used
     * @return never returns null
     * @see #getTreeNode(IModelObject, int, ITreeNode)
     */
    public List<ITreeNode> getTreeNode(final IModelObject modelObject, ITreeNode parentNode) {
        return this.getTreeNode(modelObject, parentNode.getCategories(), parentNode);
    }

    private List<ITreeNode> getTreeNode(final IModelObject modelObject, IMatch matcher) {

        List<ITreeNode> matchedTreeNodes = new ArrayList<ITreeNode>();
        List<WeakReference<ITreeNode>> treeNodes = nodeMap.get(modelObject);
        if (treeNodes != null && !treeNodes.isEmpty()) {
            Iterator<WeakReference<ITreeNode>> treeNodesIterator = treeNodes.iterator();
            while (treeNodesIterator.hasNext()) {
                WeakReference<ITreeNode> weakTreeNode = treeNodesIterator.next();
                ITreeNode treeNode = weakTreeNode.get();
                if (treeNode == null) {
                    treeNodesIterator.remove();
                } else if (matcher.isTreeNodeMatching(treeNode)) {
                    matchedTreeNodes.add(treeNode);
                }
            }
        }
        return matchedTreeNodes;

    }

    private interface IMatch {
        boolean isTreeNodeMatching(ITreeNode node);
    }

    private class CategoryMatcher implements IMatch {
        private int categories;

        CategoryMatcher(int categories) {
            this.categories = categories;
        }

        public boolean isTreeNodeMatching(ITreeNode node) {
            return (node.getCategories() & categories) != 0;
        }
    }

    private class ExcludeCategoryMatcher implements IMatch {
        private int categoriesToExclude;

        ExcludeCategoryMatcher(int categoriesToExclude) {
            this.categoriesToExclude = categoriesToExclude;
        }

        public boolean isTreeNodeMatching(ITreeNode node) {
            return (node.getCategories() & categoriesToExclude) == 0;
        }
    }

    private class ParentCategoryMatcher implements IMatch {

        private ITreeNode parentNode;
        private int categories;

        ParentCategoryMatcher(int categories, ITreeNode parentNode) {
            this.categories = categories;
            this.parentNode = parentNode;
        }

        @Override
        public boolean isTreeNodeMatching(ITreeNode node) {

            return (node.getCategories() & categories) == categories
                    && (parentNode == null ? node.getParent() == null : parentNode.equals(node.getParent()));
        }
    }

    /**
     * Returns the Category node for a given model object
     * 
     * @param category
     * @param modelObject
     * @return
     */
    public ITreeNode getCategoryNode(final String category, final Object modelObject) {
        if (categoryNodeMap.containsKey(modelObject)) {
            Map<String, ITreeNode> categoryMap = categoryNodeMap.get(modelObject);
            if (categoryMap != null) {
                if (categoryMap.containsKey(category))
                    return categoryMap.get(category);
            }
        }
        return null;
    }

    /**
     * Do always check for an existing tree node with specific categories/parent
     * node, before adding a new one.
     * 
     * @see #getTreeNode(IModelObject, int, ITreeNode)
     */
    public void addToNodeMap(final IModelObject modelObject, final ITreeNode treeNode) {
        List<WeakReference<ITreeNode>> treeNodes = nodeMap.get(modelObject);
        if (treeNodes == null) {
            treeNodes = new ArrayList<WeakReference<ITreeNode>>();
            nodeMap.put(modelObject, treeNodes);
        }
        treeNodes.add(new WeakReference<ITreeNode>(treeNode));
    }

    public void addToCategoryNodeMap(final String category, final Object modelObject, final ITreeNode node) {
        if (categoryNodeMap.containsKey(modelObject)) {
            Map<String, ITreeNode> categoryMap = categoryNodeMap.get(modelObject);
            if (categoryMap != null)
                categoryMap.put(category, node);
        } else {
            Map<String, ITreeNode> categoryMap = new HashMap<String, ITreeNode>();
            categoryMap.put(category, node);
            categoryNodeMap.put(modelObject, categoryMap);
        }
    }

    /**
     * This method removes all tree nodes associated with one model object.
     * 
     * @param modelObject
     *            for which to delete all associated tree nodes
     * @see #removeNodeFromMap(ITreeNode)
     */
    public void removeNodeFromMap(final IModelObject modelObject) {
        nodeMap.remove(modelObject);
    }

    /**
     * This method removes a single tree node instance from tree nodes map.
     * 
     * @param treeNode
     *            to remove
     */
    public void removeNodeFromMap(final ITreeNode treeNode) {
        IModelObject modelObject = treeNode.getModelObject();
        List<WeakReference<ITreeNode>> treeNodes = nodeMap.get(modelObject);

        if (treeNodes == null) {
            return;
        }

        Iterator<WeakReference<ITreeNode>> treeNodesIterator = treeNodes.iterator();
        while (treeNodesIterator.hasNext()) {
            ITreeNode treeNodeFromMap = treeNodesIterator.next().get();
            if (treeNodeFromMap == null || treeNode.equals(treeNodeFromMap)) {
                treeNodesIterator.remove();
            }
        }
    }

    // Removes from category node map
    public void removeCategoryNodeFromMap(final String category, final Object modelObject) {
        if (category != null && modelObject != null) {
            if (categoryNodeMap.containsKey(modelObject)) {
                Map<String, ITreeNode> categoryMap = categoryNodeMap.get(modelObject);
                if (categoryMap != null) {
                    if (categoryMap.containsKey(category))
                        categoryMap.remove(category);
                    if (categoryMap.isEmpty())
                        categoryNodeMap.remove(modelObject);
                }
            }
        }
    }

    public void clearAllNodesFromMap() {
        categoryNodeMap.clear();
        nodeMap.clear();
    }

    /**
     * Return the corresponding ITreeNode for a given XSDConcretComponent
     * object, or null if the ITreeNode missing
     * 
     * @param modelObject
     *            is IWsdlModelRoot( in case of SIE) or IXsdModelRoot(in case if
     *            DTE), MUST be not null
     * @param searchedObject
     *            is XsdElement
     * @return
     */
    public ITreeNode getITreeNodeForXsdElement(final IModelObject modelObject, XSDConcreteComponent searchedObject) {
        if (!EmfXsdUtils.hasCorrespondingITreeNode(searchedObject)) {
            return null;
        }

        ISchema schema = (ISchema) modelObject;
        ITreeNode schemaNode = getTreeNode(schema);
        if (schemaNode == null) {
            return null;
        }

        XSDSchema schemaComponent = schema.getComponent();
        if (schemaComponent.hashCode() == searchedObject.hashCode()) {
            return schemaNode;
        }

        Stack<EObject> emfPathToTheObject = new Stack<EObject>();
        emfPathToTheObject.push(searchedObject);

        EObject pathCreator = searchedObject.eContainer();
        while (pathCreator != schemaComponent && pathCreator != null) {
            if (EmfXsdUtils.hasCorrespondingITreeNode(pathCreator)) {
                emfPathToTheObject.push(pathCreator);
            }
            pathCreator = pathCreator.eContainer();
        }

        ITreeNode result = getXsdITreeNode(schemaNode, emfPathToTheObject);
        if (result != null) {
            return result;
        }

        return null;
    }

    /**
     * Recursive searching process in EMF path and as a result the method return
     * ITreeNode from DTPage which correspond to the bottom of the path, or null
     * otherwise
     * 
     * @param node
     *            is current processing ITreeNode
     * @param emfPathToTheObject
     *            is a path to the EMF object
     * @return ITreeNode which is part of the treeViewer in DataTypesPage
     */
    private ITreeNode getXsdITreeNode(ITreeNode node, Stack<EObject> emfPathToTheObject) {
        if (emfPathToTheObject.empty()) {
            return node;
        }
        EObject currentEObject = emfPathToTheObject.pop();
        ITreeNode treeNode = null;
        for (Object currentNode : node.getChildren()) {
            treeNode = (ITreeNode) currentNode;
            ITreeNode nodeFromEmfPath = checkXsdElement(treeNode, (XSDConcreteComponent) currentEObject);
            if (nodeFromEmfPath != null) {
                return getXsdITreeNode(nodeFromEmfPath, emfPathToTheObject);
            }
        }
        return null;
    }

    private ITreeNode checkXsdElement(ITreeNode currentTreeNode, EObject emfComponent) {
        XSDConcreteComponent currentXsdComponent = (XSDConcreteComponent) currentTreeNode.getModelObject().getComponent();
        if (currentXsdComponent.hashCode() == emfComponent.hashCode()
                || currentXsdComponent.hashCode() == ((XSDConcreteComponent) emfComponent).getContainer().hashCode()) {
            return currentTreeNode;
        }
        return null;
    }

    /**
     * Return the corresponding ITreeNode for a given EMF object, or null if the
     * ITreeNode missing
     * 
     * @param modelObject
     *            is IWsdlModelRoot, must be not null
     * @param searchedObject
     *            is WSDLElement
     * @param portTypes
     *            i s List of all ServiceInterface nodes
     * @return
     */
    public ITreeNode getITreeNodeForWsdlElement(final IModelObject modelObject, final EObject searchedObject,
            List<ServiceInterfaceNode> portTypes) {
        if (!EmfWsdlUtils.hasCorrespondingITreeNode(searchedObject)) {
            return null;
        }

        ITreeNode result = null;
        for (ITreeNode currentTreeNode : portTypes) {
            EObject emfComponentForCurrentTreeNode = currentTreeNode.getModelObject().getComponent();
            if (emfComponentForCurrentTreeNode.hashCode() == searchedObject.hashCode()) {
                return currentTreeNode;
            }
            result = getWsdlITreeNode(currentTreeNode, searchedObject);
            if (result != null) {
                return result;
            }
        }
        return result;
    }

    /**
     * Recursive search process which return corresponding ITreeNode for a given
     * WSDLElement, or null otherwise
     * 
     * @param node
     *            is and ITreeNode from the treeViewer in Service Interface Page
     * @param searchedComponent
     *            is and WSDL EMF Object
     * @return corresponding ITreeNode to the "searchedComponent"
     */
    private ITreeNode getWsdlITreeNode(final ITreeNode node, final EObject searchedComponent) {
        if (node == null || !node.hasChildren()) {
            return null;
        }
        ITreeNode currentNode;
        for (Object currentChild : node.getChildren()) {
            currentNode = (ITreeNode) currentChild;
            WSDLElement currentWsdlComponent = (WSDLElement) currentNode.getModelObject().getComponent();
            if (currentWsdlComponent.hashCode() == searchedComponent.hashCode()) {
                return currentNode;
            }

            ITreeNode treeNode = getWsdlITreeNode(currentNode, searchedComponent);
            if (treeNode != null) {
                return treeNode;
            }
        }
        return null;
    }

}
