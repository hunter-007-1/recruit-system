package com.recruit.chain.exception;

import io.github.talelin.autoconfigure.exception.HttpException;

/**
 * 区块链操作异常
 * @author SuoMingFei
 * @since 2024-09-01
 */
public class ChainException extends HttpException {

    public ChainException(int code) {
        super(code);
    }

    public ChainException(int code, int httpCode) {
        super(code, httpCode);
    }
}
