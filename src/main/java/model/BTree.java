package model;

import annatations.Exclude;
import com.google.gson.GsonBuilder;
import exceptions.NameMatchException;
import exceptions.NodeNotFoundException;
import util.GsonExclusionStrategy;
import util.StringPathUtil;

import java.util.*;

public class BTree<T> {
    static class Node<T> {
        int level;
        String name;
        @Exclude
        Node<T> root;
        T value;
        ArrayList<Node<T>> children = new ArrayList<>();

        public Node(Node<T> root) {
            this.root = root;
            if (root != null){
                root.add(this);
                this.level = root.level + 1;
            }
        }

        public Node(String name, Node<T> root, T value) {
            this(root);
            this.name = name;
            this.value = value;

        }

        public Node(String name, Node<T> root) {
            this(root);
            this.name = name;
        }

        public Node<T> search(String name) {
            if (this.name.equals(name))
                return this;
            return searchRec(this, name);
        }

        private Node<T> searchRec(Node<T> cur, String name) {
            Node<T> node1 = null;
            for (Node<T> node : cur.children) {
                if (node.name.equals(name))
                    return node;
                if (!node.children.isEmpty()) {
                    node1 = searchRec(node, name);
                    if (node1 != null)
                        break;
                }
            }
            return node1;
        }

        private void add(Node<T> node) {
            this.children.add(node);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }

    }

    private Node<T> root;
    private HashSet<String> names = new HashSet<>();

    public boolean add(String nameRoot, String name, T value) throws NodeNotFoundException, NameMatchException {
        if (this.root == null) {
            this.root = new Node<>(name, null, value);
            return true;
        }
        if (this.root.search(name) != null)
            throw new NameMatchException("Node name " + name + " already exists!");
        Node<T> node = this.root.search(nameRoot);
        if (node == null)
            throw new NodeNotFoundException("Node named " + nameRoot + " doesn't exist!");
        new Node<T>(name, node, value);
        return true;
    }

    /*public static ArrayList<FileInfo> getDifferenceBetweenBTree(BTree<FileInfo> bTree, BTree<FileInfo> bTree1) {
        Node<FileInfo> root = bTree.root;
        ArrayList<FileInfo> list = new ArrayList<>();
        fillDifferenceList(list, root, bTree1);
        return list;
    }

    private static void fillDifferenceList(ArrayList<FileInfo> list, Node<FileInfo> root, BTree<FileInfo> bTree) {
        for (Node<FileInfo> node : root.children) {
            if (!bTree.contain(StringPathUtil.getPathWithoutPathRootServerDirectory(node.getName()))) {
                list.add(node.value);
            } else {
                if (node.value.isFolder()) {
                    fillDifferenceList(list, node, bTree);
                }
            }
        }
    }*/

    public boolean contain(String name) {
        return this.root.search(name) != null;
    }

    public static ArrayList<FileInfo> getDifferenceBetweenBTree(BTree<FileInfo> bTree, BTree<FileInfo> bTreeForComparison) {
        Node<FileInfo> rootNode = bTree.root;
        ArrayList<FileInfo> list = new ArrayList<>();
        fillDifferenceList(list, rootNode, bTreeForComparison);
        return list;
    }

    private static void fillDifferenceList(ArrayList<FileInfo> fileInfoArrayList, Node<FileInfo> rootNode, BTree<FileInfo> bTreeForComparison) {
        for (Node<FileInfo> node : rootNode.children) {
            if (!bTreeForComparison.contain(node.getName())) {
                fileInfoArrayList.add(node.value);
            } else {
                if (node.value.isFolder()) {
                    fillDifferenceList(fileInfoArrayList, node, bTreeForComparison);
                } else {
                    if (!bTreeForComparison.root.search(node.getName()).value.getHash().equals(node.value.getHash())) {
                        fileInfoArrayList.add(node.value);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return new GsonBuilder().addSerializationExclusionStrategy(new GsonExclusionStrategy()).setPrettyPrinting().create().toJson(this);
    }

    /*
    public String toString() {
        TreeMap<Integer, ArrayList<String>> treeMap = new TreeMap<>(Comparator.comparingInt(o -> o));
        this.toString(treeMap, this.root);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<Integer, ArrayList<String>> entry : treeMap.entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
                str.append(entry.getValue().get(i));
            }
        }
        return str.toString();
    }

    private ArrayList<String> getListString(Node<T> node){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(node.name);
        for (Node<T> n : node.children){
            arrayList.add(" ");
            arrayList.add(n.name);
        }
        arrayList.add("\r\n");
        return arrayList;
    }

    private void toString(TreeMap<Integer, ArrayList<String>> treeMap, Node<T> curNode) {
        if (!treeMap.containsKey(curNode.level))
            treeMap.put(curNode.level, new ArrayList<>());
        treeMap.get(curNode.level).addAll(this.getListString(curNode));
        for (Node<T> node : curNode.children) {
            if (node.children.size() != 0)
                this.toString(treeMap, node);
        }
    }*/
}
