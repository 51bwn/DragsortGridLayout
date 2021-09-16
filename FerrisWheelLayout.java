
public class FerrisWheelLayout extends RelativeLayout
{
  private Paint blackPaint;
  double cellDegree;
  Point center = new Point();
  Point childCenter = new Point();
  double degree = 0.0D;
  private GestureDetector gestureDetector;
  private Paint grayPaint;
  int maxChildHeight;
  int maxChildWidth;
  int myHeight;
  int myWidth;
  private GestureDetector.OnGestureListener ogl = new GestureDetector.SimpleOnGestureListener()
  {
    double deltaDegree;

    public boolean onDown(MotionEvent paramAnonymousMotionEvent)
    {
      if ((FerrisWheelLayout.this.valueAnimator != null) && (FerrisWheelLayout.this.valueAnimator.isRunning()))
        FerrisWheelLayout.this.valueAnimator.cancel();
      return false;
    }

    public boolean onFling(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      double d = (FerrisWheelLayout.this.getTouchDegree(paramAnonymousMotionEvent2.getX() + paramAnonymousFloat1 / 1000, paramAnonymousMotionEvent2.getY() + paramAnonymousFloat2 / 1000) - FerrisWheelLayout.this.getTouchDegree(paramAnonymousMotionEvent2.getX(), paramAnonymousMotionEvent2.getY())) * 1000;
      FerrisWheelLayout.this.changeDegreeVel(d);
      return false;
    }

    public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      this.deltaDegree = (FerrisWheelLayout.this.getTouchDegree(paramAnonymousMotionEvent2.getX(), paramAnonymousMotionEvent2.getY()) - FerrisWheelLayout.this.getTouchDegree(paramAnonymousFloat1 + paramAnonymousMotionEvent2.getX(), paramAnonymousFloat2 + paramAnonymousMotionEvent2.getY()));
      FerrisWheelLayout.this.setDegree(FerrisWheelLayout.this.degree + this.deltaDegree);
      return false;
    }

    public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent)
    {
      if (FerrisWheelLayout.this.onItemClickListener != null)
      {
        View localView = FerrisWheelLayout.this.findTouchedView(paramAnonymousMotionEvent);
        if (localView != null)
          FerrisWheelLayout.this.onItemClickListener.onClick(localView);
      }
      return false;
    }
  };
  View.OnClickListener onItemClickListener;
  private Paint redPaint;
  private ValueAnimator valueAnimator;
private int radius;

  public FerrisWheelLayout(Context paramContext)
  {
    this(paramContext, null);
  }

  public FerrisWheelLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public FerrisWheelLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }

  private void computeValues()
  {
    this.myWidth = getWidth();
    this.myHeight = getHeight();
    this.maxChildWidth = 0;
    this.maxChildHeight = 0;
    this.center.x = (this.myWidth / 2);
    this.center.y = (this.myHeight / 2);
    int i = getChildCount();
    this.cellDegree = (6.283185307179586D / i);
    for (int j = 0; ; j++)
    {
      if (j >= i)
      {
        this.radius = Math.min((this.myWidth - this.maxChildWidth) / 2, (this.myHeight - this.maxChildHeight) / 2);
        return;
      }
      View localView = getChildAt(j);
      if (this.maxChildWidth < localView.getWidth())
        this.maxChildWidth = localView.getWidth();
      if (this.maxChildHeight < localView.getHeight())
        this.maxChildHeight = localView.getHeight();
    }
  }

  private View findTouchedView(MotionEvent paramMotionEvent)
  {
    int i = getChildCount();
    for (int j = 0; ; j++)
    {
      View localView;
      if (j >= i)
        localView = null;
//      do
//      {
//        return localView;
//        localView = getChildAt(j);
//      }
//      while ((paramMotionEvent.getX() >= localView.getLeft()) && (paramMotionEvent.getX() <= localView.getRight()) && (paramMotionEvent.getY() >= localView.getTop()) && (paramMotionEvent.getY() <= localView.getBottom()));
    }
  }

  private double getTouchDegree(double paramDouble1, double paramDouble2)
  {
    return Math.atan2(paramDouble2 - this.center.y, paramDouble1 - this.center.y);
  }

  private void init()
  {
    this.blackPaint = new Paint(1);
    this.blackPaint.setColor(-16777216);
    this.blackPaint.setStyle(Paint.Style.STROKE);
    this.blackPaint.setStrokeWidth(2.0F);
    this.redPaint = new Paint(1);
    this.redPaint.setColor(-65536);
    this.grayPaint = new Paint(1);
    this.grayPaint.setColor(-7829368);
    this.gestureDetector = new GestureDetector(getContext(), this.ogl);
  }

  protected void changeDegreeVel(double paramDouble)
  {
    double d = paramDouble / 6.283185307179586D * Math.abs(paramDouble / 6.283185307179586D);
    long l = (long) (1000.0D * Math.abs(paramDouble / 6.283185307179586D));
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = (float)this.degree;
    arrayOfFloat[1] = (float)(d + this.degree);
    this.valueAnimator = ValueAnimator.ofFloat(arrayOfFloat);
    Log.d("changeDegreeVel", "degree" + this.degree);
    Log.d("changeDegreeVel", "degree +degreeVel " + (paramDouble + this.degree));
    this.valueAnimator.setDuration(l);
    this.valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        FerrisWheelLayout.this.setDegree(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
      }
    });
    this.valueAnimator.setInterpolator(new DecelerateInterpolator(2.0F));
    this.valueAnimator.start();
  }

  @SuppressLint({"WrongCall"})
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    onDraw(paramCanvas);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    paramCanvas.drawCircle(this.center.x, this.center.y, (float)this.radius, this.blackPaint);
    paramCanvas.drawCircle(this.center.x, this.center.y, 6.0F, this.redPaint);
    int i = getChildCount();
    for (int j = 0; ; j++)
    {
      if (j >= i)
        return;
      this.childCenter.x = (int)(this.center.x + Math.sin(this.degree + j * this.cellDegree) * this.radius);
      this.childCenter.y = (int)(this.center.y - Math.cos(this.degree + j * this.cellDegree) * this.radius);
      paramCanvas.drawLine(this.center.x, this.center.y, this.childCenter.x, this.childCenter.y, this.grayPaint);
    }
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return true;
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    computeValues();
    int i = getChildCount();
//    Log.d("onLayout", this.radius);
    for (int j = 0; ; j++)
    {
      if (j >= i)
        return;
      this.childCenter.x = (int)(this.center.x + Math.sin(this.degree + j * this.cellDegree) * this.radius);
      this.childCenter.y = (int)(this.center.y - Math.cos(this.degree + j * this.cellDegree) * this.radius);
      View localView = getChildAt(j);
      localView.layout(this.childCenter.x - localView.getWidth() / 2, this.childCenter.y - localView.getHeight() / 2, this.childCenter.x + localView.getWidth() / 2, this.childCenter.y + localView.getHeight() / 2);
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.gestureDetector.onTouchEvent(paramMotionEvent);
    return true;
  }

  public void setDegree(double paramDouble)
  {
    this.degree = paramDouble;
    requestLayout();
  }

  public void setOnItemClickListener(View.OnClickListener paramOnClickListener)
  {
    this.onItemClickListener = paramOnClickListener;
  }
}
