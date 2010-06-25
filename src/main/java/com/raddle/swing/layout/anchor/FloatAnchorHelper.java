package com.raddle.swing.layout.anchor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import com.raddle.swing.layout.anchor.dynamic.DynamicPadding;
import com.raddle.swing.layout.anchor.dynamic.DefaultDynamicPadding;

/**
 * 类FloatAnchorHelper.java的实现描述：浮动跟随边框，跟随的边保持边距，其他边也相应的移动，保持图形大小不变
 *
 * @author xurong 2009-6-14 下午02:00:33
 */
public class FloatAnchorHelper {

    public static enum FLOAT_TYPE {
        /**
         * 绝对定位，和边框保持绝对数值的大小
         */
        ABSOLUTE,
        /**
         * 相对定位，和边框保持外框大小百分之多少的距离
         */
        RELATIVE
    };

    /**
     * 在相对定位时用
     *
     * @author xurong
     */
    public static enum LOCATION_TYPE {
        /**
         * 自己左上角和外框的左边框和上边框的相对距离
         */
        LEFT_TOP,
        /**
         * 自己中心点和外框的中心点的距离
         */
        CENTER
    };

    private Container          outer;
    private Component          self;
    private int                leftPad      = -1;
    private int                topPad       = -1;
    private int                rightPad     = -1;
    private int                bottomPad    = -1;
    private boolean            anchorLeft   = false;
    private boolean            anchorTop    = false;
    private boolean            anchorRight  = false;
    private boolean            anchorBottom = false;
    private boolean            listening    = false;
    private FLOAT_TYPE         floatType    = FLOAT_TYPE.ABSOLUTE;
    private LOCATION_TYPE      locationType = LOCATION_TYPE.LEFT_TOP;
    private float              alignmentX   = 0;
    private float              alignmentY   = 0;
    private DynamicPadding anchorOutRectangle;

    public FloatAnchorHelper(Container outer, Component self){
        this.outer = outer;
        this.self = self;
        anchorOutRectangle = new DefaultDynamicPadding(outer);
    }

    /**
     * 构建helper，左上角绝对定位浮动
     *
     * @param outer
     * @param self
     * @param leftPad 大于-1跟随左边框，左边距保持leftPad
     * @param topPad 大于-1跟随上边框，上边距保持topPad
     * @param rightPad 大于-1跟随右边框，右边距保持rightPad
     * @param bottomPad 大于-1跟随下边框，下边距保持bottomPad
     */
    public FloatAnchorHelper(Container outer, Component self, int leftPad, int topPad, int rightPad, int bottomPad){
        this.outer = outer;
        this.self = self;
        this.leftPad = leftPad;
        this.topPad = topPad;
        this.rightPad = rightPad;
        this.bottomPad = bottomPad;
        this.anchorLeft = leftPad > -1;
        this.anchorTop = topPad > -1;
        this.anchorRight = rightPad > -1;
        this.anchorBottom = bottomPad > -1;
        this.floatType = FLOAT_TYPE.ABSOLUTE;
        this.locationType = LOCATION_TYPE.LEFT_TOP;
        anchorOutRectangle = new DefaultDynamicPadding(outer);
    }

    /**
     * 构建helper，左上角绝对定位浮动
     *
     * @param outer
     * @param self
     * @param anchorLeft 跟随左边框，左边距保持不变
     * @param anchorTop 跟随上边框，上边距保持不变
     * @param anchorRight 跟随右边框，右边距保持不变
     * @param anchorBottom 跟随下边框，下边距保持不变
     */
    public FloatAnchorHelper(Container outer, Component self, boolean anchorLeft, boolean anchorTop, boolean anchorRight, boolean anchorBottom){
        this.outer = outer;
        this.self = self;
        this.anchorLeft = anchorLeft;
        this.anchorTop = anchorTop;
        this.anchorRight = anchorRight;
        this.anchorBottom = anchorBottom;
        this.floatType = FLOAT_TYPE.ABSOLUTE;
        this.locationType = LOCATION_TYPE.LEFT_TOP;
        anchorOutRectangle = new DefaultDynamicPadding(outer);
    }

    /**
     * 构建helper，相对定位浮动
     *
     * @param outer
     * @param self
     * @param locationType 最上角还是中心定位
     * @param alignmentX 左边距的百分比
     * @param alignmentY 上边距的百分比
     */
    public FloatAnchorHelper(Container outer, Component self, LOCATION_TYPE locationType, float alignmentX, float alignmentY){
        this.outer = outer;
        this.self = self;
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.floatType = FLOAT_TYPE.RELATIVE;
        this.locationType = locationType;
        anchorOutRectangle = new DefaultDynamicPadding(outer);
    }

    /**
     * 开始跟随边框
     */
    public void floating() {
        if (!listening) {
            listening = true;
            outer.addComponentListener(new ComponentAdapter() {

                public void componentResized(ComponentEvent evt) {
                    if (floatType == FLOAT_TYPE.ABSOLUTE) {
                        if (anchorLeft) {
                            if (leftPad == -1) {
                                leftPad = self.getX();
                            }
                            self.setLocation(leftPad + anchorOutRectangle.getLeftPad(), self.getY());
                        }
                        if (anchorTop) {
                            if (topPad == -1) {
                                topPad = self.getY();
                            }
                            self.setLocation(self.getX(), topPad + anchorOutRectangle.getTopPad());
                        }
                        if (anchorRight) {
                            if (rightPad == -1) {
                                rightPad = outer.getWidth() - self.getX() - self.getWidth();
                            }
                            self.setLocation(outer.getWidth() - (rightPad + anchorOutRectangle.getRightPad()) - self.getWidth(), self.getY());
                        }
                        if (anchorBottom) {
                            if (bottomPad == -1) {
                                bottomPad = outer.getHeight() - self.getY() - self.getHeight();
                            }
                            self.setLocation(self.getX(), outer.getHeight() - (bottomPad + anchorOutRectangle.getBottomPad()) - self.getHeight());
                        }
                    } else if (floatType == FLOAT_TYPE.RELATIVE) {
                        if (locationType == LOCATION_TYPE.LEFT_TOP) {
                        	if(alignmentX >= 0){
                        		self.setLocation((int) (outer.getWidth() * alignmentX) + anchorOutRectangle.getLeftPad(), self.getY());
                        	}
                        	if (alignmentY >= 0) {
                        		self.setLocation(self.getX(), (int) (outer.getHeight() * alignmentY) + anchorOutRectangle.getTopPad());
                            }
                        } else if (locationType == LOCATION_TYPE.CENTER) {
                            if (alignmentX >= 0) {
                                self.setLocation((int) (outer.getWidth() * alignmentX - self.getWidth() / 2), self.getY());
                            }
                            if (alignmentY >= 0) {
                                self.setLocation(self.getX(), (int) (outer.getHeight() * alignmentY - self.getHeight() / 2));
                            }
                        }
                    }

                }
            });
        }
    }

    public int getLeftPad() {
        return leftPad;
    }

    public void setLeftPad(int leftPad) {
        this.leftPad = leftPad;
    }

    public int getTopPad() {
        return topPad;
    }

    public void setTopPad(int topPad) {
        this.topPad = topPad;
    }

    public int getRightPad() {
        return rightPad;
    }

    public void setRightPad(int rightPad) {
        this.rightPad = rightPad;
    }

    public int getBottomPad() {
        return bottomPad;
    }

    public void setBottomPad(int bottomPad) {
        this.bottomPad = bottomPad;
    }

    public boolean isAnchorLeft() {
        return anchorLeft;
    }

    public void setAnchorLeft(boolean anchorLeft) {
        this.anchorLeft = anchorLeft;
    }

    public boolean isAnchorTop() {
        return anchorTop;
    }

    public void setAnchorTop(boolean anchorTop) {
        this.anchorTop = anchorTop;
    }

    public boolean isAnchorRight() {
        return anchorRight;
    }

    public void setAnchorRight(boolean anchorRight) {
        this.anchorRight = anchorRight;
    }

    public boolean isAnchorBottom() {
        return anchorBottom;
    }

    public void setAnchorBottom(boolean anchorBottom) {
        this.anchorBottom = anchorBottom;
    }

    public FLOAT_TYPE getFloatType() {
        return floatType;
    }

    public void setFloatType(FLOAT_TYPE floatType) {
        this.floatType = floatType;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }

    public void setLocationType(LOCATION_TYPE locationType) {
        this.locationType = locationType;
    }

    public float getAlignmentX() {
        return alignmentX;
    }

    public void setAlignmentX(float alignmentX) {
        this.alignmentX = alignmentX;
    }

    public float getAlignmentY() {
        return alignmentY;
    }

    public void setAlignmentY(float alignmentY) {
        this.alignmentY = alignmentY;
    }

    public DynamicPadding getAnchorOutRectangle() {
        return anchorOutRectangle;
    }

    public void setAnchorOutRectangle(DynamicPadding anchorOutRectangle) {
        this.anchorOutRectangle = anchorOutRectangle;
    }

}