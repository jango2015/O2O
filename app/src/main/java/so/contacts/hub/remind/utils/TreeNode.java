/**
 * @date	: 
 * @author	: change
 * @descrip	:
 */

package so.contacts.hub.remind.utils;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> implements java.io.Serializable {
    public T t;

    private TreeNode<T> parent;

    public List<TreeNode<T>> nodelist;

    public TreeNode(T stype) {
        this.t = stype;
        this.parent = null;
        this.nodelist = new ArrayList<TreeNode<T>>();
    }

    public TreeNode(T stype, TreeNode<T> parent) {
        this.t = stype;
        this.parent = parent;
        this.nodelist = new ArrayList<TreeNode<T>>();
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }
}
