/**
 * @date	: 
 * @author	: change
 * @descrip	:
 */

package so.contacts.hub.remind.utils;

public class Tree<T> implements java.io.Serializable {

    public TreeNode<T> root;

    public Tree() {
    }

    /**
     * 增加新节点
     */
    public void addNode(TreeNode<T> node, T oneNode) {
        // 增加根节点
        if (null == node) {
            if (null == root) {
                root = new TreeNode<T>(oneNode);
            }
        } else {
            TreeNode<T> temp = new TreeNode<T>(oneNode, node);
            node.nodelist.add(temp);
        }
    }

    /**
     * 在指定input节点下查找oneNode
     * @date
     * @author
     * @description
     * @params
     */
    public TreeNode<T> search(TreeNode<T> input, T oneNode) {
        TreeNode<T> temp = null;

        if (input.t.equals(oneNode)) {
            return input;
        }

        for (int i = 0; i < input.nodelist.size(); i++) {
            temp = search(input.nodelist.get(i), oneNode);
            if (null != temp) {
                break;
            }
        }

        return temp;
    }

    /**
     * 从根节点查找指定oneNode
     */
    public TreeNode<T> getNode(T oneNode) {
        return search(root, oneNode);
    }

    public void showNode(TreeNode<T> node) {
        if (null != node) {
            // 循环遍历node的节点
            System.out.println(node.t.toString());

            for (int i = 0; i < node.nodelist.size(); i++) {
                showNode(node.nodelist.get(i));
            }
        }
    }
}
