package com.lear.game2048.transfer;


import com.lear.game2048.utils.Message;

/**
 * 传递接口，主要用于Activity与Fragment之间的信息传递
 */
public interface ITransfer {

    /**
     * 传递信息
     *
     * @param self    自身
     * @param message 传递信息
     */
    void onTransferMessage(ITransfer self, Message message);
}
