/**
 * MIT License
 * 
 * Copyright (c) 2009-present GuoGang and other contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils.entity;

/**
 * MetaItemType
 * @author 郭罡
 * @description 2009-7-31 郭罡 新建
 */
public enum MetaItemType {
	NUMBER(1), VARCHAR(2), TIME(3), TABLE(10), REF(11),
	
	/**  */
	TEXT(21),  BLOB(22);

	private int code;

	private MetaItemType(int code) {
		this.code = code;
	}

	public static MetaItemType valueOf(Integer value) {
        switch (value) {
        case 1:
            return NUMBER;
        case 2:
            return VARCHAR;
        case 3:
            return TIME;
        case 10:
            return TABLE;
        case 11:
            return REF;
        case 21:
            return TEXT;
        case 22:
            return BLOB;
        default:
            return VARCHAR;
        }
    }
	@Override
	public String toString() {
		return String.valueOf(this.code);

	}
}
