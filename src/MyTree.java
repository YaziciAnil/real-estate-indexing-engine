import java.util.ArrayList;

/** AVL tree of houses; each node also stores the max size in its subtree for pruning. */
public class MyTree {

    public class TreeNode {
        House val;
        TreeNode right;
        TreeNode left;

        int height;
        int maxSubtreeSize;

        public TreeNode(House val) {
            this.val = val;
            this.height = 1;
            this.maxSubtreeSize = val.getSize();
        }
    }

    private TreeNode root;

    public MyTree() {
        this.root = null;
    }

    public TreeNode getRoot() {
        return this.root;
    }

    private int getHeight(TreeNode node) {
        return (node == null) ? 0 : node.height;
    }

    private int getMaxSubtreeSize(TreeNode node) {
        return (node == null) ? 0 : node.maxSubtreeSize;
    }

    /** Recomputes height and max subtree size from the children. */
    private void updateNode(TreeNode node) {
        if (node == null) return;

        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        int max = node.val.getSize();
        if (node.left != null) {
            max = Math.max(max, node.left.maxSubtreeSize);
        }
        if (node.right != null) {
            max = Math.max(max, node.right.maxSubtreeSize);
        }
        node.maxSubtreeSize = max;
    }

    private int getBalance(TreeNode node) {
        return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    /** Right rotation for rebalancing. */
    private TreeNode rotateRight(TreeNode node) {
        TreeNode x = node.left;
        TreeNode temp = x.right;

        x.right = node;
        node.left = temp;

        updateNode(node);
        updateNode(x);

        return x;
    }

    /** Left rotation for rebalancing. */
    private TreeNode rotateLeft(TreeNode node) {
        TreeNode x = node.right;
        TreeNode temp = x.left;

        x.left = node;
        node.right = temp;

        updateNode(node);
        updateNode(x);

        return x;
    }

    /** Inserts a house and rebalances the tree. */
    public void insert(House t) {
        this.root = insertRecursive(this.root, t);
    }

    private TreeNode insertRecursive(TreeNode current, House t) {
        if (current == null) return new TreeNode(t);

        int cmp = t.compareTo(current.val);

        // Smaller goes left, larger goes right
        if (cmp < 0) {
            current.left = insertRecursive(current.left, t);
        } else if (cmp > 0) {
            current.right = insertRecursive(current.right, t);
        } else {
            return current; // Duplicate element
        }

        updateNode(current);
        int balance = getBalance(current);

        // Left Left Case
        if (balance > 1 && t.compareTo(current.left.val) < 0) {
            return rotateRight(current);
        }

        // Right Right Case
        if (balance < -1 && t.compareTo(current.right.val) > 0) {
            return rotateLeft(current);
        }

        // Left Right Case
        if (balance > 1 && t.compareTo(current.left.val) > 0) {
            current.left = rotateLeft(current.left);
            return rotateRight(current);
        }

        // Right Left Case
        if (balance < -1 && t.compareTo(current.right.val) < 0) {
            current.right = rotateRight(current.right);
            return rotateLeft(current);
        }

        return current;
    }


    /** Returns the leftmost (smallest) node of a subtree. */
    private TreeNode getMinValueNode(TreeNode node) {
        TreeNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    /** Deletes a house and rebalances the tree. */
    public void delete(House house) {
        this.root = deleteRecursive(this.root, house);
    }

    private TreeNode deleteRecursive(TreeNode current, House house) {
        if (current == null) {
            return null;
        }

        int cmp = house.compareTo(current.val);

        if (cmp < 0) {
            current.left = deleteRecursive(current.left, house);
        } else if (cmp > 0) {
            current.right = deleteRecursive(current.right, house);
        } else {
            // Node found: remove it
            if (current.left == null || current.right == null) {
                TreeNode temp = (current.left != null) ? current.left : current.right;
                if (temp == null) {
                    current = null;
                } else {
                    current = temp;
                }
            } else {
                TreeNode temp = getMinValueNode(current.right);
                current.val = temp.val;
                current.right = deleteRecursive(current.right, temp.val);
            }
        }

        if (current == null) {
            return null;
        }

        updateNode(current);
        int balance = getBalance(current);

        // Left Left Case
        if (balance > 1 && getBalance(current.left) >= 0) {
            return rotateRight(current);
        }

        // Left Right Case
        if (balance > 1 && getBalance(current.left) < 0) {
            current.left = rotateLeft(current.left);
            return rotateRight(current);
        }

        // Right Right Case
        if (balance < -1 && getBalance(current.right) <= 0) {
            return rotateLeft(current);
        }

        // Right Left Case
        if (balance < -1 && getBalance(current.right) > 0) {
            current.right = rotateRight(current.right);
            return rotateLeft(current);
        }

        return current;
    }


    /** Collects up to count houses with price <= maxPrice and size >= minSize, in order. */
    public ArrayList<House> findP1(int maxPrice, int minSize, int count) {
        ArrayList<House> p1List = new ArrayList<>();
        collectP1(this.root, maxPrice, minSize, count, p1List);
        return p1List;
    }

    private void collectP1(TreeNode treeNode, int maxPrice, int minSize, int count, ArrayList<House> p1) {
        if (treeNode == null || p1.size() >= count) return;

        // Prune subtrees with no house large enough
        if (treeNode.maxSubtreeSize < minSize) return;

        // Left subtree first (cheaper houses)
        collectP1(treeNode.left, maxPrice, minSize, count, p1);

        if (p1.size() >= count) return;

        // Current node
        if (treeNode.val.getPrice() <= maxPrice) {
            if (treeNode.val.getSize() >= minSize) {
                p1.add(treeNode.val);
            }
            // Go right only while within budget
            collectP1(treeNode.right, maxPrice, minSize, count, p1);
        }
    }

    /** Collects up to count houses with price <= maxPrice and size < minSize, in order. */
    public ArrayList<House> findP2(int maxPrice, int minSize, int count) {
        ArrayList<House> p2List = new ArrayList<>();
        collectP2(this.root, maxPrice, minSize, count, p2List);
        return p2List;
    }

    private void collectP2(TreeNode treeNode, int maxPrice, int minSize, int count, ArrayList<House> p2) {
        if (treeNode == null || p2.size() >= count) return;

        // Left subtree first (cheaper houses)
        collectP2(treeNode.left, maxPrice, minSize, count, p2);

        if (p2.size() >= count) return;

        // Current node
        if (treeNode.val.getPrice() <= maxPrice) {
            // P2: within budget but below minimum size
            if (treeNode.val.getSize() < minSize) {
                p2.add(treeNode.val);
            }
            // Go right only while within budget
            collectP2(treeNode.right, maxPrice, minSize, count, p2);
        }
    }

    /** Collects up to count houses with price > maxPrice and size >= minSize, in order. */
    public ArrayList<House> findP3(int maxPrice, int minSize, int count) {
        ArrayList<House> p3List = new ArrayList<>();
        collectP3(this.root, maxPrice, minSize, count, p3List);
        return p3List;
    }

    private void collectP3(TreeNode treeNode, int maxPrice, int minSize, int count, ArrayList<House> p3) {
        if (treeNode == null || p3.size() >= count) return;

        // Prune subtrees with no house large enough
        if (treeNode.maxSubtreeSize < minSize) return;

        // Left subtree first (cheaper houses)
        collectP3(treeNode.left, maxPrice, minSize, count, p3);

        if (p3.size() >= count) return;

        // Current node
        if (treeNode.val.getPrice() > maxPrice) {
            if (treeNode.val.getSize() >= minSize) {
                p3.add(treeNode.val);
            }
        }

        // Right subtree (higher prices)
        collectP3(treeNode.right, maxPrice, minSize, count, p3);
    }

    /** Collects up to count houses with price > maxPrice and size < minSize, in order. */
    public ArrayList<House> findP4(int maxPrice, int minSize, int count) {
        ArrayList<House> p4List = new ArrayList<>();
        collectP4(this.root, maxPrice, minSize, count, p4List);
        return p4List;
    }

    private void collectP4(TreeNode treeNode, int maxPrice, int minSize, int count, ArrayList<House> p4) {
        if (treeNode == null || p4.size() >= count) return;

        collectP4(treeNode.left, maxPrice, minSize, count, p4);

        if (p4.size() >= count) return;

        if (treeNode.val.getPrice() > maxPrice) {
            if (treeNode.val.getSize() < minSize) {
                p4.add(treeNode.val);
            }
        }

        collectP4(treeNode.right, maxPrice, minSize, count, p4);
    }


    /** Sums prices of houses with price <= maxPrice and size >= minSize. */
    public long calculateTotalPrice(int maxPrice, int minSize) {
        return sumPriceRecursive(this.root, maxPrice, minSize);
    }

    private long sumPriceRecursive(TreeNode treeNode, int maxPrice, int minSize) {
        if (treeNode == null) {
            return 0;
        }

        if (treeNode.maxSubtreeSize < minSize) {
            return 0;
        }

        long total = 0;
        total += sumPriceRecursive(treeNode.left, maxPrice, minSize);

        if (treeNode.val.getPrice() <= maxPrice) {
            if (treeNode.val.getSize() >= minSize) {
                total += treeNode.val.getPrice();
            }
            total += sumPriceRecursive(treeNode.right, maxPrice, minSize);
        }
        return total;
    }
}