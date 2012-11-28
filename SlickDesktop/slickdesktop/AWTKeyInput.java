/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package slickdesktop;

import java.awt.event.KeyEvent;

/**
 *
 * @author bjgil
 */
public class AWTKeyInput
{
    /**
     * escape key.
     */
    public static final int KEY_ESCAPE = 0x01;
    /**
     * 1 key.
     */
    public static final int KEY_1 = 0x02;
    /**
     * 2 key.
     */
    public static final int KEY_2 = 0x03;
    /**
     * 3 key.
     */
    public static final int KEY_3 = 0x04;
    /**
     * 4 key.
     */
    public static final int KEY_4 = 0x05;
    /**
     * 5 key.
     */
    public static final int KEY_5 = 0x06;
    /**
     * 6 key.
     */
    public static final int KEY_6 = 0x07;
    /**
     * 7 key.
     */
    public static final int KEY_7 = 0x08;
    /**
     * 8 key.
     */
    public static final int KEY_8 = 0x09;
    /**
     * 9 key.
     */
    public static final int KEY_9 = 0x0A;
    /**
     * 0 key.
     */
    public static final int KEY_0 = 0x0B;
    /**
     * - key.
     */
    public static final int KEY_MINUS = 0x0C;
    /**
     * = key.
     */
    public static final int KEY_EQUALS = 0x0D;
    /**
     * back key.
     */
    public static final int KEY_BACK = 0x0E;
    /**
     * tab key.
     */
    public static final int KEY_TAB = 0x0F;
    /**
     * q key.
     */
    public static final int KEY_Q = 0x10;
    /**
     * w key.
     */
    public static final int KEY_W = 0x11;
    /**
     * e key.
     */
    public static final int KEY_E = 0x12;
    /**
     * r key.
     */
    public static final int KEY_R = 0x13;
    /**
     * t key.
     */
    public static final int KEY_T = 0x14;
    /**
     * y key.
     */
    public static final int KEY_Y = 0x15;
    /**
     * u key.
     */
    public static final int KEY_U = 0x16;
    /**
     * i key.
     */
    public static final int KEY_I = 0x17;
    /**
     * o key.
     */
    public static final int KEY_O = 0x18;
    /**
     * p key.
     */
    public static final int KEY_P = 0x19;
    /**
     * [ key.
     */
    public static final int KEY_LBRACKET = 0x1A;
    /**
     * ] key.
     */
    public static final int KEY_RBRACKET = 0x1B;
    /**
     * enter (main keyboard) key.
     */
    public static final int KEY_RETURN = 0x1C;
    /**
     * left control key.
     */
    public static final int KEY_LCONTROL = 0x1D;
    /**
     * a key.
     */
    public static final int KEY_A = 0x1E;
    /**
     * s key.
     */
    public static final int KEY_S = 0x1F;
    /**
     * d key.
     */
    public static final int KEY_D = 0x20;
    /**
     * f key.
     */
    public static final int KEY_F = 0x21;
    /**
     * g key.
     */
    public static final int KEY_G = 0x22;
    /**
     * h key.
     */
    public static final int KEY_H = 0x23;
    /**
     * j key.
     */
    public static final int KEY_J = 0x24;
    /**
     * k key.
     */
    public static final int KEY_K = 0x25;
    /**
     * l key.
     */
    public static final int KEY_L = 0x26;
    /**
     * ; key.
     */
    public static final int KEY_SEMICOLON = 0x27;
    /**
     * ' key.
     */
    public static final int KEY_APOSTROPHE = 0x28;
    /**
     * ` key.
     */
    public static final int KEY_GRAVE = 0x29;
    /**
     * left shift key.
     */
    public static final int KEY_LSHIFT = 0x2A;
    /**
     * \ key.
     */
    public static final int KEY_BACKSLASH = 0x2B;
    /**
     * z key.
     */
    public static final int KEY_Z = 0x2C;
    /**
     * x key.
     */
    public static final int KEY_X = 0x2D;
    /**
     * c key.
     */
    public static final int KEY_C = 0x2E;
    /**
     * v key.
     */
    public static final int KEY_V = 0x2F;
    /**
     * b key.
     */
    public static final int KEY_B = 0x30;
    /**
     * n key.
     */
    public static final int KEY_N = 0x31;
    /**
     * m key.
     */
    public static final int KEY_M = 0x32;
    /**
     * , key.
     */
    public static final int KEY_COMMA = 0x33;
    /**
     * . key (main keyboard).
     */
    public static final int KEY_PERIOD = 0x34;
    /**
     * / key (main keyboard).
     */
    public static final int KEY_SLASH = 0x35;
    /**
     * right shift key.
     */
    public static final int KEY_RSHIFT = 0x36;
    /**
     * * key (on keypad).
     */
    public static final int KEY_MULTIPLY = 0x37;
    /**
     * left alt key.
     */
    public static final int KEY_LMENU = 0x38;
    /**
     * space key.
     */
    public static final int KEY_SPACE = 0x39;
    /**
     * caps lock key.
     */
    public static final int KEY_CAPITAL = 0x3A;
    /**
     * F1 key.
     */
    public static final int KEY_F1 = 0x3B;
    /**
     * F2 key.
     */
    public static final int KEY_F2 = 0x3C;
    /**
     * F3 key.
     */
    public static final int KEY_F3 = 0x3D;
    /**
     * F4 key.
     */
    public static final int KEY_F4 = 0x3E;
    /**
     * F5 key.
     */
    public static final int KEY_F5 = 0x3F;
    /**
     * F6 key.
     */
    public static final int KEY_F6 = 0x40;
    /**
     * F7 key.
     */
    public static final int KEY_F7 = 0x41;
    /**
     * F8 key.
     */
    public static final int KEY_F8 = 0x42;
    /**
     * F9 key.
     */
    public static final int KEY_F9 = 0x43;
    /**
     * F10 key.
     */
    public static final int KEY_F10 = 0x44;
    /**
     * NumLK key.
     */
    public static final int KEY_NUMLOCK = 0x45;
    /**
     * Scroll lock key.
     */
    public static final int KEY_SCROLL = 0x46;
    /**
     * 7 key (num pad).
     */
    public static final int KEY_NUMPAD7 = 0x47;
    /**
     * 8 key (num pad).
     */
    public static final int KEY_NUMPAD8 = 0x48;
    /**
     * 9 key (num pad).
     */
    public static final int KEY_NUMPAD9 = 0x49;
    /**
     * - key (num pad).
     */
    public static final int KEY_SUBTRACT = 0x4A;
    /**
     * 4 key (num pad).
     */
    public static final int KEY_NUMPAD4 = 0x4B;
    /**
     * 5 key (num pad).
     */
    public static final int KEY_NUMPAD5 = 0x4C;
    /**
     * 6 key (num pad).
     */
    public static final int KEY_NUMPAD6 = 0x4D;
    /**
     * + key (num pad).
     */
    public static final int KEY_ADD = 0x4E;
    /**
     * 1 key (num pad).
     */
    public static final int KEY_NUMPAD1 = 0x4F;
    /**
     * 2 key (num pad).
     */
    public static final int KEY_NUMPAD2 = 0x50;
    /**
     * 3 key (num pad).
     */
    public static final int KEY_NUMPAD3 = 0x51;
    /**
     * 0 key (num pad).
     */
    public static final int KEY_NUMPAD0 = 0x52;
    /**
     * . key (num pad).
     */
    public static final int KEY_DECIMAL = 0x53;
    /**
     * F11 key.
     */
    public static final int KEY_F11 = 0x57;
    /**
     * F12 key.
     */
    public static final int KEY_F12 = 0x58;
    /**
     * F13 key.
     */
    public static final int KEY_F13 = 0x64;
    /**
     * F14 key.
     */
    public static final int KEY_F14 = 0x65;
    /**
     * F15 key.
     */
    public static final int KEY_F15 = 0x66;
    /**
     * kana key (Japanese).
     */
    public static final int KEY_KANA = 0x70;
    /**
     * convert key (Japanese).
     */
    public static final int KEY_CONVERT = 0x79;
    /**
     * noconvert key (Japanese).
     */
    public static final int KEY_NOCONVERT = 0x7B;
    /**
     * yen key (Japanese).
     */
    public static final int KEY_YEN = 0x7D;
    /**
     * = on num pad (NEC PC98).
     */
    public static final int KEY_NUMPADEQUALS = 0x8D;
    /**
     * circum flex key (Japanese).
     */
    public static final int KEY_CIRCUMFLEX = 0x90;
    /**
     * &#064; key (NEC PC98).
     */
    public static final int KEY_AT = 0x91;
    /**
     * : key (NEC PC98)
     */
    public static final int KEY_COLON = 0x92;
    /**
     * _ key (NEC PC98).
     */
    public static final int KEY_UNDERLINE = 0x93;
    /**
     * kanji key (Japanese).
     */
    public static final int KEY_KANJI = 0x94;
    /**
     * stop key (NEC PC98).
     */
    public static final int KEY_STOP = 0x95;
    /**
     * ax key (Japanese).
     */
    public static final int KEY_AX = 0x96;
    /**
     * (J3100).
     */
    public static final int KEY_UNLABELED = 0x97;
    /**
     * Enter key (num pad).
     */
    public static final int KEY_NUMPADENTER = 0x9C;
    /**
     * right control key.
     */
    public static final int KEY_RCONTROL = 0x9D;
    /**
     * , key on num pad (NEC PC98).
     */
    public static final int KEY_NUMPADCOMMA = 0xB3;
    /**
     * / key (num pad).
     */
    public static final int KEY_DIVIDE = 0xB5;
    /**
     * SysRq key.
     */
    public static final int KEY_SYSRQ = 0xB7;
    /**
     * right alt key.
     */
    public static final int KEY_RMENU = 0xB8;
    /**
     * pause key.
     */
    public static final int KEY_PAUSE = 0xC5;
    /**
     * home key.
     */
    public static final int KEY_HOME = 0xC7;
    /**
     * up arrow key.
     */
    public static final int KEY_UP = 0xC8;
    /**
     * PgUp key.
     */
    public static final int KEY_PRIOR = 0xC9;
    /**
     * PgUp key.
     */
    public static final int KEY_PGUP = KEY_PRIOR;

    /**
     * left arrow key.
     */
    public static final int KEY_LEFT = 0xCB;
    /**
     * right arrow key.
     */
    public static final int KEY_RIGHT = 0xCD;
    /**
     * end key.
     */
    public static final int KEY_END = 0xCF;
    /**
     * down arrow key.
     */
    public static final int KEY_DOWN = 0xD0;
    /**
     * PgDn key.
     */
    public static final int KEY_NEXT = 0xD1;
    /**
     * PgDn key.
     */
    public static final int KEY_PGDN = KEY_NEXT;

    /**
     * insert key.
     */
    public static final int KEY_INSERT = 0xD2;
    /**
     * delete key.
     */
    public static final int KEY_DELETE = 0xD3;
    /**
     * left windows key.
     */
    public static final int KEY_LWIN = 0xDB;
    /**
     * right windows key.
     */
    public static final int KEY_RWIN = 0xDC;
    /**
     * menu key.
     */
    public static final int KEY_APPS = 0xDD;
    /**
     * power key.
     */
    public static final int KEY_POWER = 0xDE;
    /**
     * sleep key.
     */
    public static final int KEY_SLEEP = 0xDF;
    
    /**
     * <code>toAWTCode</code> converts KeyInput key codes to AWT key codes.
     *
     * @param key jme KeyInput key code
     * @return awt KeyEvent key code
     */
    public static int toAWTCode( int key ) {
        switch ( key ) {
            case KEY_ESCAPE:
                return KeyEvent.VK_ESCAPE;
            case KEY_1:
                return KeyEvent.VK_1;
            case KEY_2:
                return KeyEvent.VK_2;
            case KEY_3:
                return KeyEvent.VK_3;
            case KEY_4:
                return KeyEvent.VK_4;
            case KEY_5:
                return KeyEvent.VK_5;
            case KEY_6:
                return KeyEvent.VK_6;
            case KEY_7:
                return KeyEvent.VK_7;
            case KEY_8:
                return KeyEvent.VK_8;
            case KEY_9:
                return KeyEvent.VK_9;
            case KEY_0:
                return KeyEvent.VK_0;
            case KEY_MINUS:
                return KeyEvent.VK_MINUS;
            case KEY_EQUALS:
                return KeyEvent.VK_EQUALS;
            case KEY_BACK:
                return KeyEvent.VK_BACK_SPACE;
            case KEY_TAB:
                return KeyEvent.VK_TAB;
            case KEY_Q:
                return KeyEvent.VK_Q;
            case KEY_W:
                return KeyEvent.VK_W;
            case KEY_E:
                return KeyEvent.VK_E;
            case KEY_R:
                return KeyEvent.VK_R;
            case KEY_T:
                return KeyEvent.VK_T;
            case KEY_Y:
                return KeyEvent.VK_Y;
            case KEY_U:
                return KeyEvent.VK_U;
            case KEY_I:
                return KeyEvent.VK_I;
            case KEY_O:
                return KeyEvent.VK_O;
            case KEY_P:
                return KeyEvent.VK_P;
            case KEY_LBRACKET:
                return KeyEvent.VK_OPEN_BRACKET;
            case KEY_RBRACKET:
                return KeyEvent.VK_CLOSE_BRACKET;
            case KEY_RETURN:
                return KeyEvent.VK_ENTER;
            case KEY_LCONTROL:
                return KeyEvent.VK_CONTROL;
            case KEY_A:
                return KeyEvent.VK_A;
            case KEY_S:
                return KeyEvent.VK_S;
            case KEY_D:
                return KeyEvent.VK_D;
            case KEY_F:
                return KeyEvent.VK_F;
            case KEY_G:
                return KeyEvent.VK_G;
            case KEY_H:
                return KeyEvent.VK_H;
            case KEY_J:
                return KeyEvent.VK_J;
            case KEY_K:
                return KeyEvent.VK_K;
            case KEY_L:
                return KeyEvent.VK_L;
            case KEY_SEMICOLON:
                return KeyEvent.VK_SEMICOLON;
            case KEY_APOSTROPHE:
                return KeyEvent.VK_QUOTE;
            case KEY_GRAVE:
                return KeyEvent.VK_DEAD_GRAVE;
            case KEY_LSHIFT:
                return KeyEvent.VK_SHIFT;
            case KEY_BACKSLASH:
                return KeyEvent.VK_BACK_SLASH;
            case KEY_Z:
                return KeyEvent.VK_Z;
            case KEY_X:
                return KeyEvent.VK_X;
            case KEY_C:
                return KeyEvent.VK_C;
            case KEY_V:
                return KeyEvent.VK_V;
            case KEY_B:
                return KeyEvent.VK_B;
            case KEY_N:
                return KeyEvent.VK_N;
            case KEY_M:
                return KeyEvent.VK_M;
            case KEY_COMMA:
                return KeyEvent.VK_COMMA;
            case KEY_PERIOD:
                return KeyEvent.VK_PERIOD;
            case KEY_SLASH:
                return KeyEvent.VK_SLASH;
            case KEY_RSHIFT:
                return KeyEvent.VK_SHIFT;
            case KEY_MULTIPLY:
                return KeyEvent.VK_MULTIPLY;
            case KEY_SPACE:
                return KeyEvent.VK_SPACE;
            case KEY_CAPITAL:
                return KeyEvent.VK_CAPS_LOCK;
            case KEY_F1:
                return KeyEvent.VK_F1;
            case KEY_F2:
                return KeyEvent.VK_F2;
            case KEY_F3:
                return KeyEvent.VK_F3;
            case KEY_F4:
                return KeyEvent.VK_F4;
            case KEY_F5:
                return KeyEvent.VK_F5;
            case KEY_F6:
                return KeyEvent.VK_F6;
            case KEY_F7:
                return KeyEvent.VK_F7;
            case KEY_F8:
                return KeyEvent.VK_F8;
            case KEY_F9:
                return KeyEvent.VK_F9;
            case KEY_F10:
                return KeyEvent.VK_F10;
            case KEY_NUMLOCK:
                return KeyEvent.VK_NUM_LOCK;
            case KEY_SCROLL:
                return KeyEvent.VK_SCROLL_LOCK;
            case KEY_NUMPAD7:
                return KeyEvent.VK_NUMPAD7;
            case KEY_NUMPAD8:
                return KeyEvent.VK_NUMPAD8;
            case KEY_NUMPAD9:
                return KeyEvent.VK_NUMPAD9;
            case KEY_SUBTRACT:
                return KeyEvent.VK_SUBTRACT;
            case KEY_NUMPAD4:
                return KeyEvent.VK_NUMPAD4;
            case KEY_NUMPAD5:
                return KeyEvent.VK_NUMPAD5;
            case KEY_NUMPAD6:
                return KeyEvent.VK_NUMPAD6;
            case KEY_ADD:
                return KeyEvent.VK_ADD;
            case KEY_NUMPAD1:
                return KeyEvent.VK_NUMPAD1;
            case KEY_NUMPAD2:
                return KeyEvent.VK_NUMPAD2;
            case KEY_NUMPAD3:
                return KeyEvent.VK_NUMPAD3;
            case KEY_NUMPAD0:
                return KeyEvent.VK_NUMPAD0;
            case KEY_DECIMAL:
                return KeyEvent.VK_DECIMAL;
            case KEY_F11:
                return KeyEvent.VK_F11;
            case KEY_F12:
                return KeyEvent.VK_F12;
            case KEY_F13:
                return KeyEvent.VK_F13;
            case KEY_F14:
                return KeyEvent.VK_F14;
            case KEY_F15:
                return KeyEvent.VK_F15;
            case KEY_KANA:
                return KeyEvent.VK_KANA;
            case KEY_CONVERT:
                return KeyEvent.VK_CONVERT;
            case KEY_NOCONVERT:
                return KeyEvent.VK_NONCONVERT;
            case KEY_NUMPADEQUALS:
                return KeyEvent.VK_EQUALS;
            case KEY_CIRCUMFLEX:
                return KeyEvent.VK_CIRCUMFLEX;
            case KEY_AT:
                return KeyEvent.VK_AT;
            case KEY_COLON:
                return KeyEvent.VK_COLON;
            case KEY_UNDERLINE:
                return KeyEvent.VK_UNDERSCORE;
            case KEY_STOP:
                return KeyEvent.VK_STOP;
            case KEY_NUMPADENTER:
                return KeyEvent.VK_ENTER;
            case KEY_RCONTROL:
                return KeyEvent.VK_CONTROL;
            case KEY_NUMPADCOMMA:
                return KeyEvent.VK_COMMA;
            case KEY_DIVIDE:
                return KeyEvent.VK_DIVIDE;
            case KEY_PAUSE:
                return KeyEvent.VK_PAUSE;
            case KEY_HOME:
                return KeyEvent.VK_HOME;
            case KEY_UP:
                return KeyEvent.VK_UP;
            case KEY_PRIOR:
                return KeyEvent.VK_PAGE_UP;
            case KEY_LEFT:
                return KeyEvent.VK_LEFT;
            case KEY_RIGHT:
                return KeyEvent.VK_RIGHT;
            case KEY_END:
                return KeyEvent.VK_END;
            case KEY_DOWN:
                return KeyEvent.VK_DOWN;
            case KEY_NEXT:
                return KeyEvent.VK_PAGE_DOWN;
            case KEY_INSERT:
                return KeyEvent.VK_INSERT;
            case KEY_DELETE:
                return KeyEvent.VK_DELETE;
            case KEY_LMENU:
                return KeyEvent.VK_ALT; //todo: location left
            case KEY_RMENU:
                return KeyEvent.VK_ALT; //todo: location right
        }
        return 0x10000 + key;
    }

}
