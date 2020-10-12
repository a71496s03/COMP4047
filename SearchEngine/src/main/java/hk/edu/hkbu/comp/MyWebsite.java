package hk.edu.hkbu.comp;

public class MyWebsite {
	public String url,title;
	AVLTree tree=new AVLTree();
	
	
	public MyWebsite(String url,String title) {
		this.url=url;
		this.title=title;
	}
	
	public void add(String word, int position) {
		tree.root=tree.insert(tree.root, word, position);
	}
	
	public void output() {
		System.out.println("Preorder traversal of constructed tree is : "); 
		tree.preOrder(tree.root);
	};
}