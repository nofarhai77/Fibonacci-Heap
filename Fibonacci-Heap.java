/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers. 
 */
/**
312495328, Nofar Haim, nofarhaim
206962912, Nizan Shami, nizans
*/
public class FibonacciHeap
{
	private HeapNode trees;
	private HeapNode min;
	private int size;
	private int numTrees = 0;
	private int marks = 0;
	private static int linksNum = 0;
	private static int cutsNum = 0;

	public FibonacciHeap() {
		this.trees = null;
		this.min = null;
		this.size = 0;
	}
	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean isEmpty()
	{
		return trees == null && size == 0;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
	 * 
	 * Returns the new node created. 
	 */
	public HeapNode insert(int key)
	{   
		HeapNode node = new HeapNode(key);
		if(this.isEmpty()) {
			this.trees = node;
			node.setNext(node);
			node.setPrev(node);
			this.min = node;
			this.numTrees++;
			size++;
			return node;
		}
		if(this.findMin().getKey() > key) {
			this.min = node;
		}
		//adding node in the head of the trees linkList 
		node.next = this.trees;
		node.setPrev(this.trees.getprev());
		node.getprev().setNext(node);
		this.trees.setPrev(node);
		this.trees = node;
		this.size++;
		this.numTrees++;
		return node;
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin()
	{
		if(this.isEmpty()) {
			return;
		}
		if(this.size == 1) {
			this.trees = null;
			this.min = null;
			this.size--;
			this.numTrees--;
			return;
		}
		HeapNode min = this.findMin();
		if(min.getRank() == 0) {
			min.getprev().setNext(min.next);
			min.getnext().setPrev(min.getprev());
			if(this.findMin() == this.trees) {
				this.trees = min.getnext();
			}
		}else {
			HeapNode childs = min.child;
			min.getprev().setNext(childs);
			HeapNode cur = childs; 
			for(int i = 0; i < min.getRank();i++) {
				cur.setParent(null);
				cur = cur.getnext();
				if(cur.getMark()) {
					cur.setMark();
					this.marks--;
				}
			}
			childs.getprev().setNext(min.getnext());
			min.getnext().setPrev(childs.getprev());
			if(this.findMin() == this.trees) {
				this.trees = min.getnext();
			}else {
				childs.setPrev(min.getprev());
			}
			
		}
		Consolidating();
		this.size--;
		return; 

	}

	private void Consolidating() {
		this.numTrees = 0;
		HeapNode[] roots = toBuckats();
		formBuckets(roots);
	}

	private void formBuckets(HeapNode[] roots) {
		HeapNode head = null;
		this.min.setKey(Integer.MAX_VALUE);
		for(HeapNode node : roots) {
			if(node != (null)) {
				if(node.getKey() <= this.findMin().getKey()) {
					this.min = node;
				}
				if(head == (null)) {
					head = node;
					head.setNext(head);
					head.setPrev(head);
					this.numTrees++;
				}else {
					this.numTrees++;
					//insert after
					head.getprev().setNext(node);
					node.setNext(head);
					node.setPrev(head.getprev());
					head.setPrev(node);
				}
			}
		}
		this.trees = head;
	}
	private HeapNode[] toBuckats() {
		int len = (int) Math.floor((Math.log(this.size()) / Math.log(1.4) + 1));
		HeapNode[] roots = new HeapNode[len];
		this.trees.getprev().setNext(null);
		HeapNode node = this.trees;
		while(node != null) {
			HeapNode y = node;
			node = node.getnext();
			while(roots[y.getRank()] != null) {
				y = link(y,roots[y.getRank()]);
				roots[y.getRank()-1] = null;
			}
			roots[y.getRank()] = y;
		}
		return roots;

	}
	private HeapNode link(HeapNode x, HeapNode y) {
		linksNum++;
		if(x.getKey() > y.getKey()) {
			linksNum--;
			return link(y,x);
		}
		if(x.getchild() == null) {
			x.setChild(y);
			y.setNext(y);
			y.setPrev(y);
		}else {
			y.setNext(x.getchild());
			y.setPrev(x.getchild().getprev());
			x.getchild().getprev().setNext(y);
			x.getchild().setPrev(y);
			x.setChild(y);
		}
		y.setParent(x);
		x.setRank(x.getRank() + 1);
		return x;
	}
	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal. 
	 *
	 */
	public HeapNode findMin()
	{
		return this.min;
	} 

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld (FibonacciHeap heap2)
	{
		if(heap2.findMin().getKey() < this.min.getKey()) {
			this.min = heap2.min;
		}

		HeapNode head = heap2.trees;
		HeapNode tail = heap2.trees.getprev();
		this.trees.getprev().setNext(head);
		tail.setNext(this.trees);
		head.setPrev(this.trees.getprev());
		this.trees.setPrev(tail);
		this.numTrees = this.numTrees  + heap2.numTrees;
		this.marks = this.marks + heap2.marks;
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return this.size; 
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
	 * 
	 */
	public int[] countersRep()
	{
		if(size() == 0) {
			return new int[0];
		}
		int stop = this.trees.getKey();
		int len = (int) Math.floor((Math.log(this.size()) / Math.log(1.4) + 1));
		int[] arr = new int[len];
		HeapNode node = this.trees;
		arr[node.getRank()]++;
		node = node.getnext();
		while(node.getKey() != stop) {
			arr[node.getRank()]++;
			node = node.getnext();
		}
		return arr; //	 to be replaced by student code
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap. 
	 *
	 */
	public void delete(HeapNode x) 
	{    
		decreaseKey(x, Integer.MIN_VALUE);
		deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the heap should be updated
	 * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta)
	{    
		x.setKey(x.getKey() - delta);
		if(x.getKey() < findMin().getKey()){
			this.min = x;
		}
		if(x.getparent() != null && x.getKey() < x.getparent().getKey()) {
			cascadingCuts(x, x.getparent());
		}
	}

	private void cascadingCuts(HeapNode x, HeapNode y) {
		cut(x,y);
		if(y.getparent() != null){
			if(y.getMark()) {
				cascadingCuts(y, y.getparent());
			}else {
				y.setMark();
				this.marks++;
			}
		}
		
	}
	private void cut(HeapNode x, HeapNode y) {
		cutsNum++;
		x.setParent(null);
		if(x.getMark()) {
			x.setMark();
			this.marks--;
		}
		y.setRank(y.getRank() -1);
		if(x.getnext().equals(x)) {
			y.setChild(null);
		}else {
			y.setChild(x.getnext());
		}
		//delete x form childs list
		x.getprev().setNext(x.getnext());
		x.getnext().setPrev(x.getprev());
		//adding x to the head of the list
		x.setNext(this.trees);
		x.setPrev(this.trees.getprev());
		this.trees.setPrev(x);
		x.getprev().setNext(x);
		this.trees = x;
		this.numTrees++;
	}
	/**
	 * public int potential() 
	 *
	 * This function returns the current potential of the heap, which is:
	 * Potential = #trees + 2*#marked
	 * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
	 */
	public int potential() 
	{    
		return this.numTrees + 2*this.marks; // should be replaced by student code
	}

	/**
	 * public static int totalLinks() 
	 *
	 * This static function returns the total number of link operations made during the run-time of the program.
	 * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
	 * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
	 * in its root.
	 */
	public static int totalLinks()
	{    
		return linksNum; // should be replaced by student code
	}

	/**
	 * public static int totalCuts() 
	 *
	 * This static function returns the total number of cut operations made during the run-time of the program.
	 * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
	 */
	public static int totalCuts()
	{    
		return cutsNum; // should be replaced by student code
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k) 
	 *
	 * This static function returns the k minimal elements in a binomial tree H.
	 * The function should run in O(k*deg(H)). 
	 * You are not allowed to change H.
	 */
	public static int[] kMin(FibonacciHeap H, int k)
	{    
		int count = 0;
		int[] result = new int[k];
		FibonacciHeap Kheap = new FibonacciHeap();
		HeapNode node = Kheap.insert(H.trees.getKey());
		node.setRealNode(H.trees);
		while(count < k) {
			HeapNode minNode = Kheap.findMin();
			Kheap.deleteMin();
			result[count] = minNode.getKey();
			count++;
			minNode = minNode.getRealNode().getchild();
			if(minNode != null) {
				HeapNode tmp = Kheap.insert(minNode.getKey());
				tmp.setRealNode(minNode);
				int nodeKey = minNode.getKey();
				minNode = minNode.getnext();
				while(minNode.getKey() != nodeKey) {
					tmp = Kheap.insert(minNode.getKey());
					tmp.setRealNode(minNode);
					minNode = minNode.getnext();
				}	
			}			
		}
		return result;
	}

	
	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap
	 * (for example HeapNode), do it in this file, not in 
	 * another file 
	 *  
	 */
	public class HeapNode{

		private HeapNode realNode;
		private int key;	
		private int rank;
		private boolean mark;
		private HeapNode parent;
		private HeapNode child;
		private HeapNode next;
		private HeapNode prev;

		public HeapNode(int key) {
			this.key = key;
			this.mark = false;
		}

		public int getKey() {
			return this.key;
		}
		public void setKey(int k) {
			this.key = k;
		}

		public int getRank() {
			return this.rank;
		}

		public void setRank(int r) {
			this.rank = r;
		}

		public boolean getMark() {
			return this.mark;
		}

		public HeapNode getRealNode() {
			return realNode;
		}

		public void setRealNode(HeapNode realNode) {
			this.realNode = realNode;
		}

		public void setMark() {
			this.mark = !this.mark;
		}

		public HeapNode getparent() {
			return this.parent;
		}

		public void setParent(HeapNode p) {
			this.parent = p;
		}

		public HeapNode getchild() {
			return this.child;
		}

		public void setChild(HeapNode c) {
			this.child = c;
		}

		public HeapNode getnext() {
			return this.next;
		}

		public void setNext(HeapNode n) {
			this.next = n;
		}


		public HeapNode getprev() {
			return this.prev;
		}

		public void setPrev(HeapNode pev) {
			this.prev = pev;
		}
	}
}