package com.lear.game2048.view_model;


import androidx.collection.ArraySet;

import com.lear.game2048.model.GameBlockUnit;
import com.lear.game2048.model.GameTypeModel;
import com.lear.game2048.utils.MathCalculationUtils;

import java.io.Serializable;
import java.util.Random;
import java.util.Set;

public class GameDataModel implements Serializable {

    public static final String TAG = "GameDataModel";

    private boolean DEBUG = false;


    public static final int GAME_STATUS_WIN = 1;        //游戏胜利
    public static final int GAME_STATUS_LOSE = 2;      //游戏失败
    public static final int GAME_STATUS_CONTINUE = 3;   //游戏继续

    private GameBlockUnit[][] mBaseUnitArray;        //基础单元组
    private GameBlockUnit[][] mNewUnitArray;         //新单元组
    private GameBlockUnit[][] mBgUnit;      //背景单元
    private Set<GameBlockUnit> mTempSet;  //临时列表，用于保存新建缩放与移动缩放
    private GameTypeModel mGameType;        //游戏类型
    private int mDirection;                 //方向

    private int mCanvasSize;            //画布大小
    private int mBlockMargin, mBlockSize;

    private int mLevel;                     //等级，用于判断游戏胜利的条件
    private int mMaxBlockNumber;            //最大块数量
    private int mCurrentBlockNumber;        //当前块数量，用于判断游戏失败的条件
    private Random mRandom;

    //块累计器，记录是的从一开始到游戏结束所产生的块的数量
    private int mCount;

    private long mRunTime;          //运算时间

    //与创建块有关的
    private boolean isFirst;        //第一次创建块

    //运行计数器
    private long mRunCount;

    //阶段判定
    private boolean isMove, isScale, isCreate, isEnd;

    private static final int sMaxMoveTime = 100;//最大移动时间
    private static final int sMaxScaleTime = 50;//最大缩放时间
    private static final int sMaxCreateTime = 50;//最大创建时间

    private int mScore = 0;//得分

    private int mCurrentGameStatus;//当前游戏状态

    public GameDataModel(GameTypeModel type, long runTime) {
        this.mRunTime = runTime;

        this.mGameType = type;
        //初始化数组
        this.mBaseUnitArray = new GameBlockUnit[type.getCheckerBoardMax()][type.getCheckerBoardMax()];
        this.mNewUnitArray = new GameBlockUnit[type.getCheckerBoardMax()][type.getCheckerBoardMax()];
        this.mBgUnit = new GameBlockUnit[type.getCheckerBoardMax()][type.getCheckerBoardMax()];
        for (int i = 0; i < type.getCheckerBoardMax(); i++) {
            for (int j = 0; j < type.getCheckerBoardMax(); j++) {
                this.mBaseUnitArray[i][j] = new GameBlockUnit();
                this.mNewUnitArray[i][j] = new GameBlockUnit();
                this.mBgUnit[i][j] = new GameBlockUnit();
            }
        }

        mCount = 0;

        this.mDirection = -1;
        this.mLevel = 0; // 当前最大等级


        this.mRandom = new Random();
        this.mTempSet = new ArraySet<>();

        this.isFirst = true;
        this.isMove = this.isCreate = this.isEnd = false;
        this.isScale = true;
        this.mCurrentBlockNumber = 0;
        this.mRunCount = sMaxMoveTime + sMaxScaleTime;
        this.mMaxBlockNumber = type.getCheckerBoardMax() * type.getCheckerBoardMax();

        mCurrentGameStatus = GAME_STATUS_CONTINUE;
    }

    /**
     * 移动块
     */
    private void moveBlock() {
        int size = mGameType.getCheckerBoardMax();
        int index;
        //方向是否为左右滑动
        boolean direction_left_right = mDirection == MathCalculationUtils.DIRECTION_LEFT
                || mDirection == MathCalculationUtils.DIRECTION_RIGHT;

        for (int i = 0; i < size; i++) {
            index = 0;
            for (int j = 0; j < size; j++) {

                GameBlockUnit baseUnit = getBlockUnit(mBaseUnitArray, i, j);//
                GameBlockUnit newUnit;

                if (baseUnit.level != 0) {
                    //临时索引，用于记录位置，因为index有可能会变化
                    int tempIndex = index;
                    newUnit = getBlockUnit(mNewUnitArray, i, tempIndex);

                    //如果等级相同且状态为存在，则升级
                    if (baseUnit.level == newUnit.level
                            && newUnit.status == GameBlockUnit.STATUS_EXISTS) {
                        newUnit.level++;
                        newUnit.status = GameBlockUnit.STATUS_UP;

                        index++;
                        mCurrentBlockNumber--;
                        mLevel = Math.max(newUnit.level, mLevel);

                        //计分
                        mScore += (int) Math.pow(2, newUnit.level);
//                        mScore += (int) Math.pow(2, newUnit.level);
                    } else if (newUnit.status == GameBlockUnit.STATUS_EXISTS) {
                        //如果等级不同但状态为存在，则说明当前方块已经存在一个数了
                        //需要把索引指向下一位
                        index++;
                        newUnit = getBlockUnit(mNewUnitArray, i, index);
                        newUnit.level = baseUnit.level;
                        newUnit.status = GameBlockUnit.STATUS_EXISTS;
                    } else {
                        newUnit.level = baseUnit.level;
                        newUnit.status = GameBlockUnit.STATUS_EXISTS;
                    }

                    //如果是左右移动
                    if (direction_left_right) {
                        baseUnit.toX = baseUnit.baseX == newUnit.baseX ? 0 : newUnit.baseX;
                        if (baseUnit.toX != 0) mTempSet.add(baseUnit);
                    } else {
                        baseUnit.toY = baseUnit.baseY == newUnit.baseY ? 0 : newUnit.baseY;
                        if (baseUnit.toY != 0) mTempSet.add(baseUnit);
                    }

                }

            }
        }

    }

    /**
     * 计算移动块
     */
    private void calculationMoveBlock() {
        float schedule = Math.min(1, (float) mRunCount / sMaxMoveTime);
        for (GameBlockUnit unit : mTempSet) {
            if (mDirection == MathCalculationUtils.DIRECTION_LEFT ||
                    mDirection == MathCalculationUtils.DIRECTION_RIGHT) {
                unit.offset = (int) ((unit.toX - unit.baseX) * schedule);
            } else {
                unit.offset = (int) ((unit.toY - unit.baseY) * schedule);
            }
        }

    }

    /**
     * 移动结束
     * 清除多余数据
     */
    private void endMoveBlock() {
        mTempSet.clear();
        int size = mGameType.getCheckerBoardMax();
        GameBlockUnit baseUnit, newUnit;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                baseUnit = getBlockUnit(mBaseUnitArray, i, j);
                newUnit = getBlockUnit(mNewUnitArray, i, j);
                baseUnit.level = newUnit.level;
                baseUnit.status = newUnit.status;
                baseUnit.clear();
                if (baseUnit.status == GameBlockUnit.STATUS_UP)
                    mTempSet.add(baseUnit);
            }
        }
    }

    /**
     * 合并块
     */
    private void mergerBlock() {
        endMoveBlock();
    }

    /**
     * 计算合并块
     */
    private void calculationMergerBlock() {
        // TODO: 2020/8/22 这里使用二次函数可以使逻辑简单化
        float scale;

        float temp = (float) mRunCount - sMaxMoveTime;
        float general = sMaxScaleTime >> 1;

        if (temp < general) {
            scale = 1 + Math.min(0.2f, 0.2f * (temp / general));
        } else {
            scale = 1 + Math.max(0f, 0.1f - 0.1f * ((temp - general) / general));
        }

        for (GameBlockUnit unit : mTempSet) {
            unit.scale = scale;
        }
    }

    /**
     * 结束合并块
     */
    private void endMergerBlock() {
        for (GameBlockUnit unit : mTempSet) {
            unit.status = GameBlockUnit.STATUS_EXISTS;
            unit.clear();
        }
        mTempSet.clear();
    }

    /**
     * 创建新块
     */
    private void createBlock() {

        if (mCurrentGameStatus != GAME_STATUS_CONTINUE) return;
        if (DEBUG) {
            DEBUG = false;
            isFirst = false;
            debugCreateBlock();
            return;
        }

        int x, y;

        //如果是第一次
        if (isFirst) {
            isFirst = false;
            createBlock();
        }
        //循环判断,直到找到不冲突的为此
        do {
            x = mRandom.nextInt(mGameType.getCheckerBoardMax());
            y = mRandom.nextInt(mGameType.getCheckerBoardMax());
        } while (mBaseUnitArray[x][y].status != GameBlockUnit.STATUS_VOID);

        //改变状态
        mBaseUnitArray[x][y].level = mCount < 50 || mRandom.nextInt(10) != 9 ? 1 : 2;
//        mBaseUnitArray[x][y].level = mCount < 25 || mRandom.nextInt(10) != 9 ? 9 : 8;
        mBaseUnitArray[x][y].status = GameBlockUnit.STATUS_NEW;
        mBaseUnitArray[x][y].scale = 0.0f;
        mTempSet.add(mBaseUnitArray[x][y]);
        mCurrentBlockNumber++;
        mCount++;
    }

    private void debugCreateBlock() {
        int x = 0;
        int y = 0;
        //改变状态
        mBaseUnitArray[x][y].level = 1;
        mBaseUnitArray[x][y].status = GameBlockUnit.STATUS_NEW;
        mBaseUnitArray[x][y].scale = 0.0f;
        mTempSet.add(mBaseUnitArray[x][y]);
        mCurrentBlockNumber++;
        mCount++;

        x = 1;
        y = 1;
        mBaseUnitArray[x][y].level = 6;
        mBaseUnitArray[x][y].status = GameBlockUnit.STATUS_NEW;
        mBaseUnitArray[x][y].scale = 0.0f;
        mTempSet.add(mBaseUnitArray[x][y]);
        mCurrentBlockNumber++;
        mCount++;

        x = 2;
        y = 2;
        mBaseUnitArray[x][y].level = 8;
        mBaseUnitArray[x][y].status = GameBlockUnit.STATUS_NEW;
        mBaseUnitArray[x][y].scale = 0.0f;
        mTempSet.add(mBaseUnitArray[x][y]);
        mCurrentBlockNumber++;
        mCount++;

        x = 3;
        y = 3;
        mBaseUnitArray[x][y].level = 10;
        mBaseUnitArray[x][y].status = GameBlockUnit.STATUS_NEW;
        mBaseUnitArray[x][y].scale = 0.0f;
        mTempSet.add(mBaseUnitArray[x][y]);
        mCurrentBlockNumber++;
        mCount++;
    }

    /**
     * 缩放新块
     */
    private void calculationCreateBlock() {
        float scale = (float) mRunCount / (sMaxMoveTime + sMaxScaleTime + sMaxCreateTime);
        scale = Math.min(scale, 1.0f);

        for (GameBlockUnit unit : mTempSet) {
            unit.scale = scale;
        }
    }

    /**
     * 结束
     * 结束时重置所有的状态
     */
    private void end() {
        for (GameBlockUnit unit : mTempSet) {
            unit.status = GameBlockUnit.STATUS_EXISTS;
            unit.clear();
        }
        resetNewBlockUnitArray();
        mTempSet.clear();

        if (mLevel == mGameType.getMaxLevel()) {
            mCurrentGameStatus = GAME_STATUS_WIN;
        } else if (mCurrentBlockNumber >= mMaxBlockNumber && isLose()) {
            mCurrentGameStatus = GAME_STATUS_LOSE;
        } else {
            mCurrentGameStatus = GAME_STATUS_CONTINUE;
        }
    }

    /**
     * 根据x坐标与y坐标获取单元
     *
     * @param arr 二维数组
     * @param x   mBaseUnitArray的第一维数组索引
     * @param y   mBaseUnitArray的第二维数组索引
     * @return GameBlockUnit
     */
    private GameBlockUnit getBlockUnit(GameBlockUnit[][] arr, int x, int y) {
        if (mDirection == MathCalculationUtils.DIRECTION_LEFT) {
            return arr[x][y];
        } else if (mDirection == MathCalculationUtils.DIRECTION_TOP) {
            return arr[y][x];
        } else if (mDirection == MathCalculationUtils.DIRECTION_RIGHT) {
            return arr[x][mGameType.getCheckerBoardMax() - y - 1];
        } else {
            return arr[mGameType.getCheckerBoardMax() - y - 1][x];
        }
    }

    /**
     * 重置NewBlockUnitArray
     */
    private void resetNewBlockUnitArray() {
        for (int i = 0, size = mGameType.getCheckerBoardMax(); i < size; i++) {
            for (int j = 0; j < size; j++) {
                mNewUnitArray[i][j].level = 0;
                mNewUnitArray[i][j].status = GameBlockUnit.STATUS_VOID;
                mNewUnitArray[i][j].clear();
            }
        }
    }

    /**
     * 获取更新数据
     *
     * @return GameBlockUnit
     */
    public synchronized GameBlockUnit[][] getUpdateData() {
        if (isEnd || mCurrentGameStatus != GAME_STATUS_CONTINUE) return mBaseUnitArray;

        mRunCount += mRunTime;
        if (!isMove && mRunCount < sMaxMoveTime) {
            isMove = true;
            moveBlock();
        } else if (!isScale
                && mRunCount < sMaxMoveTime + sMaxScaleTime && mRunCount >= sMaxMoveTime) {
            isMove = false;
            isScale = true;
            mergerBlock();
        } else if (!isCreate
                && mRunCount < sMaxMoveTime + sMaxScaleTime + sMaxCreateTime
                && mRunCount >= sMaxMoveTime + sMaxScaleTime) {
            isScale = false;
            isCreate = true;
            endMergerBlock();
            createBlock();
        } else if (mRunCount >= sMaxMoveTime + sMaxScaleTime + sMaxCreateTime) {
            isCreate = false;
            isEnd = true;
            end();
        }

        if (mTempSet.isEmpty()) {
            if (isMove) {
                /*如果移动阶段临时集合里不存在元素，就代表着无法移动
                 * 提前结束*/
                mRunCount = sMaxMoveTime + sMaxScaleTime + sMaxCreateTime + 1;
                isMove = false;
            } else if (isScale) {
                /*如果缩放阶段临时集合里不存在元素，就代表着没有升级元素
                 * 直接跳到创建阶段*/
                mRunCount = sMaxMoveTime + sMaxScaleTime;
                isScale = false;
            }
        }

        if (isMove) {
            calculationMoveBlock();
        } else if (isScale) {
            calculationMergerBlock();
        } else if (isCreate) {
            calculationCreateBlock();
        }

        return mBaseUnitArray;

    }

    /**
     * 设置方向
     *
     * @param direction 方向
     */
    public void setDirection(int direction) {
        mDirection = direction;
        isEnd = false;
        mRunCount = 0;
    }

    public void setCanvasSize(int size) {
        mCanvasSize = size;
        //块间距
        mBlockMargin = mCanvasSize / (20 + 10 * (mGameType.getCheckerBoardMax() - 3));
        //块大小
        mBlockSize = (mCanvasSize - mBlockMargin * (mGameType.getCheckerBoardMax() + 1)) / mGameType.getCheckerBoardMax();

        int temp = mBlockMargin + mBlockSize;

        for (int i = 0; i < mGameType.getCheckerBoardMax(); i++) {      //y轴
            for (int j = 0; j < mGameType.getCheckerBoardMax(); j++) {  //x轴
                mBgUnit[i][j].baseX = mBlockMargin + j * temp;
                mBgUnit[i][j].baseY = mBlockMargin + i * temp;
                mBgUnit[i][j].width = mBgUnit[i][j].height = mBlockSize;

                mBaseUnitArray[i][j].baseX = mNewUnitArray[i][j].baseX = mBlockMargin + j * temp;
                mBaseUnitArray[i][j].baseY = mNewUnitArray[i][j].baseY = mBlockMargin + i * temp;
                mBaseUnitArray[i][j].width = mBaseUnitArray[i][j].height =
                        mNewUnitArray[i][j].width = mNewUnitArray[i][j].height = mBlockSize;

            }
        }
    }

    public GameBlockUnit[][] getBgUnit() {
        return mBgUnit;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public int getScore() {
        return mScore;
    }

    /**
     * 获取游戏结果
     *
     * @return 游戏胜利返回{GAME_STATUS_WIN}，
     * 游戏失败返回{GAME_STATUS_LOSE}，
     * 游戏继续返回{GAME_STATUS_CONTINUE}
     */
    public int getGameResult() {
        return mCurrentGameStatus;
    }

    /**
     * 判断游戏是否结束
     *
     * @return 如果没有可合并的块，则返回true
     */
    private boolean isLose() {
        for (int i = 0, size = mGameType.getCheckerBoardMax(); i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i != 0 && mBaseUnitArray[i][j].level == mBaseUnitArray[i - 1][j].level)
                        || (i != size - 1 && mBaseUnitArray[i][j].level == mBaseUnitArray[i + 1][j].level)
                        || (j != 0 && mBaseUnitArray[i][j].level == mBaseUnitArray[i][j - 1].level)
                        || (j != size - 1 && mBaseUnitArray[i][j].level == mBaseUnitArray[i][j + 1].level))
                    return false;
            }
        }
        return true;
    }

}
