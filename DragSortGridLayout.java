/**
 * XML里面指定的columncount是无效的，用setColumnCount()
 * @author luozheng
 *
 */
public class DragSortGridLayout extends GridLayout {
	private static final String TAG = "DragSortGridLayout";
	public int COLUMN_COUNT=4;//无法获得每一个宽度可以用这个/4
	private List<? extends  IDragItem> mListDragItem;//被操作的数据
	private Context context;
	private boolean mAllowDrag;//是否允许脱宅
	private View mDragView;//被拖拽的控件


	public DragSortGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	
		this.context=context;
//		this.setLayoutAnimation(controller);
		init();
	}

	public DragSortGridLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public DragSortGridLayout(Context context) {
		this(context,null);
		
	}
	private void init() {
		setColumnCount(COLUMN_COUNT);
		setLayoutTransition(new LayoutTransition());//这样等于在xml里面写的        android:animateLayoutChanges="true"
		
	}
	public void setAllowDrag(boolean value){
		this.mAllowDrag=value;
		//	// 意味着两件事情：
		// 要对gridlayout设置监听
		// 要对孩子设置长按事件，不能再此方法中设置，因为child会交换， 那咋搞，但是在布局里面他每次执行这样好吗如果
		//这个不是如果已经设置了监听onDragListener 那么每次都这样搞?? 但是没有办法咯
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		if(mAllowDrag)
		{
			setOnDragListener(mOnDragListener);
		}else{
			setOnDragListener(null);
		}
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).setOnLongClickListener(mAllowDrag?mOnLongClickListener:null);
		}
	}
	/**
	 * 设置items
	 * @param listDragItem
	 */
	public void setItems(final List<? extends IDragItem> listDragItem)
	{
		this.mListDragItem=listDragItem;
		removeAllViews();//为了防止重复叠加先清除所有
		//不要用newHnader的post,  放到post里面才能获取到width，不过不知道用测量可以不可以
		post(new Runnable() {
			
			@Override
			public void run() {
				addItemViews(listDragItem);
				Log.i(TAG,"宽度"+getWidth());
			}
		});
		
	}
	/**
	 * 获取排序后的item
	 * @return
	 */
	public List<? extends IDragItem> getItems(){
		List<  IDragItem>  listDragItem=new ArrayList<IDragItem>();
			for (int i = 0; i <getChildCount(); i++) {
				IDragItem iDragItem = (IDragItem) getChildAt(i).getTag();
				listDragItem.add(iDragItem);
			}
		return listDragItem;
//		return this.listDragItem;
	}
	private void addItemViews(List<? extends IDragItem> listDragItem) {
		for (IDragItem iDragItem : listDragItem) {
			addItemView(iDragItem);
		}
	}

	private int MARGIN=5;//每一个格子宽度
	/**
	 * 根据一个iDragItem生成一个view添加到gridLayout中去
	 * @param iDragItem
	 */
	public void addItemView(IDragItem iDragItem) {
		TextView tv=new TextView(context);
		tv.setTag(iDragItem);
		LayoutParams layoutParams=new LayoutParams();
		layoutParams.width=getWidth()/this.COLUMN_COUNT-2*this.MARGIN;//加上边距和
		layoutParams.height=LayoutParams.WRAP_CONTENT;
		layoutParams.setMargins(this.MARGIN, this.MARGIN, this.MARGIN, this.MARGIN);
		tv.setBackgroundResource(R.drawable.dsgl_normal_bg);
		tv.setGravity(Gravity.CENTER);
		tv.setText(iDragItem.getItemName());
		tv.setTextSize(14);//指定的是sp也就是缩放像素
		tv.setOnClickListener(mOnClickListener);
		Log.i(TAG, "addItemView");
		addView(tv,layoutParams);//放到最后面
//		addView(tv,0,layoutParams);
	}

	public interface IDragItem{
		public String getItemName();
	}
	
	Rect[] mChildRects;

	private void initRect() {
		int childCount =getChildCount();//获取布局中孩子的总数
		mChildRects = new Rect[childCount];//批量new矩形数组。。。然后给这数组的孩子 组个赋值高宽
		for (int i = 0; i < childCount; i++) {
			View child =getChildAt(i);
			mChildRects[i] = new Rect(child.getLeft(), child.getTop(),
					child.getRight(), child.getBottom());
		}
	}
	//查找gridlayout里面view的总数  也就是通过event.getx和gety判断这个坐标是不是在里面矩形的区域
	private int findTouchViewIndex(DragEvent event) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (mChildRects[i].contains((int) event.getX(),
					(int) event.getY())) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 拖拽监听
	 */
	private OnDragListener mOnDragListener=new OnDragListener() {
		
		@Override
		public boolean onDrag(View v, DragEvent event) {
			
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				if(mDragView!=null)
				{
					mDragView.setBackgroundResource(R.drawable.dsgl_draged_bg);
				}
				initRect();//初始化矩形。
				Log.i(TAG, "拖拽事件开始了");
				break;
			case DragEvent.ACTION_DRAG_LOCATION://
				int index;
				if((index=findTouchViewIndex(event))!=-1 && mDragView!=null && getChildAt(index)!= mDragView)
				{
					removeView(mDragView);
					addView(mDragView, index);//然后把他加到这个位置，俺么如果 反过来写会发生什么呢？ 提示已经有了一个父亲了，所以还是要解除父子关系的
				}
//				else{//试试乱套的感觉 //移到外边的位置
//					mDragView.setBackgroundColor(Color.RED);
//					removeView(mDragView);
//					addView(mDragView);//  然后把他加
//				}
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				if(mDragView!=null)
				{mDragView.setBackgroundResource(R.drawable.dsgl_normal_bg);
				}
				break;
			default:
				break;
			}
			return true;//这里也要返回true
		}
	};
	/**
	 * 长按那些矩形的监听 长按后就能让拖拽开始了
	 */
	private OnLongClickListener mOnLongClickListener=new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			
			DragSortGridLayout.this.mDragView=v;
			v.startDrag(null, new DragShadowBuilder(v), null, 0);//new的v就是阴影形成的那个v 也就是图形副本吧。
			return true;
		}
	};
	
	private OnClickListener mOnClickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mOnItemClickListener!=null)
			{
				mOnItemClickListener.onItemClick(v, (IDragItem) v.getTag());
			}
		}
	};
	public interface OnItemClickListener{
		public void onItemClick(View v,IDragItem dragItem);
	}
	public void setOnItemClickListener(OnItemClickListener onItemClickListener)
	{
		this.mOnItemClickListener=onItemClickListener;
	}
	private OnItemClickListener mOnItemClickListener=null;
	
}
