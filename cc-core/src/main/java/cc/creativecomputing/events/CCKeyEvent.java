/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.events;

/**
 * An event which indicates that a keystroke occurred in a component.
 * <p>
 * This event is generated when the application is focused and a key is 
 * pressed, released, or typed. The event is passed to every <code>CCKeyListener</code>
 * object which registered to receive such events.</p>
 * <p>
 * <em>"Key typed" events</em> are higher-level and generally do not depend on 
 * the platform or keyboard layout.  They are generated when a Unicode character 
 * is entered, and are the preferred way to find out about character input.
 * In the simplest case, a key typed event is produced by a single key press 
 * (e.g., 'a').  No key typed events are generated for keys that don't generate Unicode 
 * characters (e.g., action keys, modifier keys, etc.).</p>
 * <p>
 * The getKeyChar method always returns a valid Unicode character or 
 * CHAR_UNDEFINED.  Character input is reported by KEY_TYPED events: 
 * KEY_PRESSED and KEY_RELEASED events are not necessarily associated 
 * with character input.  Therefore, the result of the getKeyChar method 
 * is guaranteed to be meaningful only for KEY_TYPED events.   
 * <p>
 * For key pressed and key released events, the getKeyCode method returns 
 * the event's keyCode.  For key typed events, the getKeyCode method 
 * always returns VK_UNDEFINED.
 *</p>
 * <p>
 * <em>"Key pressed" and "key released" events</em> are lower-level and depend 
 * on the platform and keyboard layout. They are generated whenever a key is 
 * pressed or released, and are the only way to find out about keys that don't 
 * generate character input (e.g., action keys, modifier keys, etc.). The key 
 * being pressed or released is indicated by the getKeyCode method, which returns 
 * a virtual key code.
 *</p>
 * <p>
 * <em>Virtual key codes</em> are used to report which keyboard key has
 * been pressed, rather than a character generated by the combination
 * of one or more keystrokes (such as "A", which comes from shift and "a").  
 *</p>
 * <p>
 * For example, pressing the Shift key will cause a KEY_PRESSED event 
 * with a VK_SHIFT keyCode, while pressing the 'a' key will result in 
 * a VK_A keyCode.  After the 'a' key is released, a KEY_RELEASED event 
 * will be fired with VK_A. Separately, a KEY_TYPED event with a keyChar 
 * value of 'A' is generated.
 *</p>
 * @nosuperclasses
 */
public class CCKeyEvent extends CCEvent{
	
	public static final String KEY_EVENT = "KEY_EVENT";
	
	public static enum CCKeyEventType{
		PRESSED,
		RELEASED,
		TYPED
	}
	
	public static enum CCKeyCode{
		 /* Virtual key codes. */

	    VK_ENTER 			( '\n'),
	    VK_BACK_SPACE     ('\b'),
	    VK_TAB            ('\t'),
	    VK_CANCEL         (0x03),
	    VK_CLEAR          (0x0C),
	    VK_SHIFT          (0x10),
	    VK_CONTROL        (0x11),
	    VK_ALT            (0x12),
	    VK_PAUSE          (0x13),
	    VK_CAPS_LOCK      (0x14),
	    VK_ESCAPE         (0x1B),
	    VK_SPACE          (0x20),
	    VK_PAGE_UP        (0x21),
	    VK_PAGE_DOWN      (0x22),
	    VK_END            (0x23),
	    VK_HOME           (0x24),

	    /**
	     * Constant for the non-numpad <b>left</b> arrow key.
	     * @see #VK_KP_LEFT
	     */
	    VK_LEFT           (0x25),

	    /**
	     * Constant for the non-numpad <b>up</b> arrow key.
	     * @see #VK_KP_UP
	     */
	    VK_UP             (0x26),

	    /**
	     * Constant for the non-numpad <b>right</b> arrow key.
	     * @see #VK_KP_RIGHT
	     */
	    VK_RIGHT          (0x27),

	    /**
	     * Constant for the non-numpad <b>down</b> arrow key.
	     * @see #VK_KP_DOWN
	     */
	    VK_DOWN           (0x28),

	    /**
	     * Constant for the comma key, ","
	     */
	    VK_COMMA          (0x2C),

	    /**
	     * Constant for the minus key, "-"
	     * @since 1.2
	     */
	    VK_MINUS          (0x2D),

	    /**
	     * Constant for the period key, "."
	     */
	    VK_PERIOD         (0x2E),

	    /**
	     * Constant for the forward slash key, "/"
	     */
	    VK_SLASH          (0x2F),

	    /** VK_0 thru VK_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
	    VK_0              (0x30),
	    VK_1              (0x31),
	    VK_2              (0x32),
	    VK_3              (0x33),
	    VK_4              (0x34),
	    VK_5              (0x35),
	    VK_6              (0x36),
	    VK_7              (0x37),
	    VK_8              (0x38),
	    VK_9              (0x39),

	    /**
	     * Constant for the semicolon key, "),"
	     */
	    VK_SEMICOLON      (0x3B),

	    /**
	     * Constant for the equals key, "="
	     */
	    VK_EQUALS         (0x3D),

	    /** VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
	    VK_A              (0x41),
	    VK_B              (0x42),
	    VK_C              (0x43),
	    VK_D              (0x44),
	    VK_E              (0x45),
	    VK_F              (0x46),
	    VK_G              (0x47),
	    VK_H              (0x48),
	    VK_I              (0x49),
	    VK_J              (0x4A),
	    VK_K              (0x4B),
	    VK_L              (0x4C),
	    VK_M              (0x4D),
	    VK_N              (0x4E),
	    VK_O              (0x4F),
	    VK_P              (0x50),
	    VK_Q              (0x51),
	    VK_R              (0x52),
	    VK_S              (0x53),
	    VK_T              (0x54),
	    VK_U              (0x55),
	    VK_V              (0x56),
	    VK_W              (0x57),
	    VK_X              (0x58),
	    VK_Y              (0x59),
	    VK_Z              (0x5A),

	    /**
	     * Constant for the open bracket key, "["
	     */
	    VK_OPEN_BRACKET   (0x5B),

	    /**
	     * Constant for the back slash key, "\"
	     */
	    VK_BACK_SLASH     (0x5C),

	    /**
	     * Constant for the close bracket key, "]"
	     */
	    VK_CLOSE_BRACKET  (0x5D),

	    VK_NUMPAD0        (0x60),
	    VK_NUMPAD1        (0x61),
	    VK_NUMPAD2        (0x62),
	    VK_NUMPAD3        (0x63),
	    VK_NUMPAD4        (0x64),
	    VK_NUMPAD5        (0x65),
	    VK_NUMPAD6        (0x66),
	    VK_NUMPAD7        (0x67),
	    VK_NUMPAD8        (0x68),
	    VK_NUMPAD9        (0x69),
	    VK_MULTIPLY       (0x6A),
	    VK_ADD            (0x6B),

	    /**
	     * This constant is obsolete, and is included only for backwards
	     * compatibility.
	     * @see #VK_SEPARATOR
	     */
	    VK_SEPARATER      (0x6C),

	    /**
	     * Constant for the Numpad Separator key.
	     * @since 1.4
	     */
	    VK_SEPARATOR      (VK_SEPARATER._myCode),

	    VK_SUBTRACT       (0x6D),
	    VK_DECIMAL        (0x6E),
	    VK_DIVIDE         (0x6F),
	    VK_DELETE         (0x7F), /* ASCII DEL */
	    VK_NUM_LOCK       (0x90),
	    VK_SCROLL_LOCK    (0x91),

	    /** Constant for the F1 function key. */
	    VK_F1             (0x70),

	    /** Constant for the F2 function key. */
	    VK_F2             (0x71),

	    /** Constant for the F3 function key. */
	    VK_F3             (0x72),

	    /** Constant for the F4 function key. */
	    VK_F4             (0x73),

	    /** Constant for the F5 function key. */
	    VK_F5             (0x74),

	    /** Constant for the F6 function key. */
	    VK_F6             (0x75),

	    /** Constant for the F7 function key. */
	    VK_F7             (0x76),

	    /** Constant for the F8 function key. */
	    VK_F8             (0x77),

	    /** Constant for the F9 function key. */
	    VK_F9             (0x78),

	    /** Constant for the F10 function key. */
	    VK_F10            (0x79),

	    /** Constant for the F11 function key. */
	    VK_F11            (0x7A),

	    /** Constant for the F12 function key. */
	    VK_F12            (0x7B),

	    /**
	     * Constant for the F13 function key.
	     * @since 1.2
	     */
	    /* F13 - F24 are used on IBM 3270 keyboard), use random range for constants. */
	    VK_F13            (0xF000),

	    /**
	     * Constant for the F14 function key.
	     * @since 1.2
	     */
	    VK_F14            (0xF001),

	    /**
	     * Constant for the F15 function key.
	     * @since 1.2
	     */
	    VK_F15            (0xF002),

	    /**
	     * Constant for the F16 function key.
	     * @since 1.2
	     */
	    VK_F16            (0xF003),

	    /**
	     * Constant for the F17 function key.
	     * @since 1.2
	     */
	    VK_F17            (0xF004),

	    /**
	     * Constant for the F18 function key.
	     * @since 1.2
	     */
	    VK_F18            (0xF005),

	    /**
	     * Constant for the F19 function key.
	     * @since 1.2
	     */
	    VK_F19            (0xF006),

	    /**
	     * Constant for the F20 function key.
	     * @since 1.2
	     */
	    VK_F20            (0xF007),

	    /**
	     * Constant for the F21 function key.
	     * @since 1.2
	     */
	    VK_F21            (0xF008),

	    /**
	     * Constant for the F22 function key.
	     * @since 1.2
	     */
	    VK_F22            (0xF009),

	    /**
	     * Constant for the F23 function key.
	     * @since 1.2
	     */
	    VK_F23            (0xF00A),

	    /**
	     * Constant for the F24 function key.
	     * @since 1.2
	     */
	    VK_F24            (0xF00B),

	    VK_PRINTSCREEN    (0x9A),
	    VK_INSERT         (0x9B),
	    VK_HELP           (0x9C),
	    VK_META           (0x9D),

	    VK_BACK_QUOTE     (0xC0),
	    VK_QUOTE          (0xDE),

	    /**
	     * Constant for the numeric keypad <b>up</b> arrow key.
	     * @see #VK_UP
	     * @since 1.2
	     */
	    VK_KP_UP          (0xE0),

	    /**
	     * Constant for the numeric keypad <b>down</b> arrow key.
	     * @see #VK_DOWN
	     * @since 1.2
	     */
	    VK_KP_DOWN        (0xE1),

	    /**
	     * Constant for the numeric keypad <b>left</b> arrow key.
	     * @see #VK_LEFT
	     * @since 1.2
	     */
	    VK_KP_LEFT        (0xE2),

	    /**
	     * Constant for the numeric keypad <b>right</b> arrow key.
	     * @see #VK_RIGHT
	     * @since 1.2
	     */
	    VK_KP_RIGHT       (0xE3),

	    /* For European keyboards */
	    /** @since 1.2 */
	    VK_DEAD_GRAVE               (0x80),
	    /** @since 1.2 */
	    VK_DEAD_ACUTE               (0x81),
	    /** @since 1.2 */
	    VK_DEAD_CIRCUMFLEX          (0x82),
	    /** @since 1.2 */
	    VK_DEAD_TILDE               (0x83),
	    /** @since 1.2 */
	    VK_DEAD_MACRON              (0x84),
	    /** @since 1.2 */
	    VK_DEAD_BREVE               (0x85),
	    /** @since 1.2 */
	    VK_DEAD_ABOVEDOT            (0x86),
	    /** @since 1.2 */
	    VK_DEAD_DIAERESIS           (0x87),
	    /** @since 1.2 */
	    VK_DEAD_ABOVERING           (0x88),
	    /** @since 1.2 */
	    VK_DEAD_DOUBLEACUTE         (0x89),
	    /** @since 1.2 */
	    VK_DEAD_CARON               (0x8a),
	    /** @since 1.2 */
	    VK_DEAD_CEDILLA             (0x8b),
	    /** @since 1.2 */
	    VK_DEAD_OGONEK              (0x8c),
	    /** @since 1.2 */
	    VK_DEAD_IOTA                (0x8d),
	    /** @since 1.2 */
	    VK_DEAD_VOICED_SOUND        (0x8e),
	    /** @since 1.2 */
	    VK_DEAD_SEMIVOICED_SOUND    (0x8f),

	    /** @since 1.2 */
	    VK_AMPERSAND                (0x96),
	    /** @since 1.2 */
	    VK_ASTERISK                 (0x97),
	    /** @since 1.2 */
	    VK_QUOTEDBL                 (0x98),
	    /** @since 1.2 */
	    VK_LESS                     (0x99),

	    /** @since 1.2 */
	    VK_GREATER                  (0xa0),
	    /** @since 1.2 */
	    VK_BRACELEFT                (0xa1),
	    /** @since 1.2 */
	    VK_BRACERIGHT               (0xa2),

	    /**
	     * Constant for the "@" key.
	     * @since 1.2
	     */
	    VK_AT                       (0x0200),

	    /**
	     * Constant for the ":" key.
	     * @since 1.2
	     */
	    VK_COLON                    (0x0201),

	    /**
	     * Constant for the "^" key.
	     * @since 1.2
	     */
	    VK_CIRCUMFLEX               (0x0202),

	    /**
	     * Constant for the "$" key.
	     * @since 1.2
	     */
	    VK_DOLLAR                   (0x0203),

	    /**
	     * Constant for the Euro currency sign key.
	     * @since 1.2
	     */
	    VK_EURO_SIGN                (0x0204),

	    /**
	     * Constant for the "!" key.
	     * @since 1.2
	     */
	    VK_EXCLAMATION_MARK         (0x0205),

	    /**
	     * Constant for the inverted exclamation mark key.
	     * @since 1.2
	     */
	    VK_INVERTED_EXCLAMATION_MARK (0x0206),

	    /**
	     * Constant for the "(" key.
	     * @since 1.2
	     */
	    VK_LEFT_PARENTHESIS         (0x0207),

	    /**
	     * Constant for the "#" key.
	     * @since 1.2
	     */
	    VK_NUMBER_SIGN              (0x0208),

	    /**
	     * Constant for the "+" key.
	     * @since 1.2
	     */
	    VK_PLUS                     (0x0209),

	    /**
	     * Constant for the ")" key.
	     * @since 1.2
	     */
	    VK_RIGHT_PARENTHESIS        (0x020A),

	    /**
	     * Constant for the "_" key.
	     * @since 1.2
	     */
	    VK_UNDERSCORE               (0x020B),

	    /**
	     * Constant for the Microsoft Windows "Windows" key.
	     * It is used for both the left and right version of the key.
	     * @see #getKeyLocation()
	     * @since 1.5
	     */
	    VK_WINDOWS                  (0x020C),

	    /**
	     * Constant for the Microsoft Windows Context Menu key.
	     * @since 1.5
	     */
	    VK_CONTEXT_MENU             (0x020D),

	    /* for input method support on Asian Keyboards */

	    /* not clear what this means - listed in Microsoft Windows API */
	    VK_FINAL                    (0x0018),

	    /** Constant for the Convert function key. */
	    /* Japanese PC 106 keyboard, Japanese Solaris keyboard: henkan */
	    VK_CONVERT                  (0x001C),

	    /** Constant for the Don't Convert function key. */
	    /* Japanese PC 106 keyboard: muhenkan */
	    VK_NONCONVERT               (0x001D),

	    /** Constant for the Accept or Commit function key. */
	    /* Japanese Solaris keyboard: kakutei */
	    VK_ACCEPT                   (0x001E),

	    /* not clear what this means - listed in Microsoft Windows API */
	    VK_MODECHANGE               (0x001F),

	    /* replaced by VK_KANA_LOCK for Microsoft Windows and Solaris),
	       might still be used on other platforms */
	    VK_KANA                     (0x0015),

	    /* replaced by VK_INPUT_METHOD_ON_OFF for Microsoft Windows and Solaris),
	       might still be used for other platforms */
	    VK_KANJI                    (0x0019),

	    /**
	     * Constant for the Alphanumeric function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: eisuu */
	    VK_ALPHANUMERIC             (0x00F0),

	    /**
	     * Constant for the Katakana function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: katakana */
	    VK_KATAKANA                 (0x00F1),

	    /**
	     * Constant for the Hiragana function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: hiragana */
	    VK_HIRAGANA                 (0x00F2),

	    /**
	     * Constant for the Full-Width Characters function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: zenkaku */
	    VK_FULL_WIDTH               (0x00F3),

	    /**
	     * Constant for the Half-Width Characters function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: hankaku */
	    VK_HALF_WIDTH               (0x00F4),

	    /**
	     * Constant for the Roman Characters function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard: roumaji */
	    VK_ROMAN_CHARACTERS         (0x00F5),

	    /**
	     * Constant for the All Candidates function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard - VK_CONVERT + ALT: zenkouho */
	    VK_ALL_CANDIDATES           (0x0100),

	    /**
	     * Constant for the Previous Candidate function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard - VK_CONVERT + SHIFT: maekouho */
	    VK_PREVIOUS_CANDIDATE       (0x0101),

	    /**
	     * Constant for the Code Input function key.
	     * @since 1.2
	     */
	    /* Japanese PC 106 keyboard - VK_ALPHANUMERIC + ALT: kanji bangou */
	    VK_CODE_INPUT               (0x0102),

	    /**
	     * Constant for the Japanese-Katakana function key.
	     * This key switches to a Japanese input method and selects its Katakana input mode.
	     * @since 1.2
	     */
	    /* Japanese Macintosh keyboard - VK_JAPANESE_HIRAGANA + SHIFT */
	    VK_JAPANESE_KATAKANA        (0x0103),

	    /**
	     * Constant for the Japanese-Hiragana function key.
	     * This key switches to a Japanese input method and selects its Hiragana input mode.
	     * @since 1.2
	     */
	    /* Japanese Macintosh keyboard */
	    VK_JAPANESE_HIRAGANA        (0x0104),

	    /**
	     * Constant for the Japanese-Roman function key.
	     * This key switches to a Japanese input method and selects its Roman-Direct input mode.
	     * @since 1.2
	     */
	    /* Japanese Macintosh keyboard */
	    VK_JAPANESE_ROMAN           (0x0105),

	    /**
	     * Constant for the locking Kana function key.
	     * This key locks the keyboard into a Kana layout.
	     * @since 1.3
	     */
	    /* Japanese PC 106 keyboard with special Windows driver - eisuu + Control), Japanese Solaris keyboard: kana */
	    VK_KANA_LOCK                (0x0106),

	    /**
	     * Constant for the input method on/off key.
	     * @since 1.3
	     */
	    /* Japanese PC 106 keyboard: kanji. Japanese Solaris keyboard: nihongo */
	    VK_INPUT_METHOD_ON_OFF      (0x0107),

	    /* for Sun keyboards */
	    /** @since 1.2 */
	    VK_CUT                      (0xFFD1),
	    /** @since 1.2 */
	    VK_COPY                     (0xFFCD),
	    /** @since 1.2 */
	    VK_PASTE                    (0xFFCF),
	    /** @since 1.2 */
	    VK_UNDO                     (0xFFCB),
	    /** @since 1.2 */
	    VK_AGAIN                    (0xFFC9),
	    /** @since 1.2 */
	    VK_FIND                     (0xFFD0),
	    /** @since 1.2 */
	    VK_PROPS                    (0xFFCA),
	    /** @since 1.2 */
	    VK_STOP                     (0xFFC8),

	    /**
	     * Constant for the Compose function key.
	     * @since 1.2
	     */
	    VK_COMPOSE                  (0xFF20),

	    /**
	     * Constant for the AltGraph function key.
	     * @since 1.2
	     */
	    VK_ALT_GRAPH                (0xFF7E),

	    /**
	     * Constant for the Begin key.
	     * @since 1.5
	     */
	    VK_BEGIN                    (0xFF58),

	    /**
	     * This value is used to indicate that the keyCode is unknown.
	     * KEY_TYPED events do not have a keyCode value; this value
	     * is used instead.
	     */
	    VK_UNDEFINED(0x0);

	    private int _myCode;
	    
	    private CCKeyCode(int theCode){
	    	_myCode = theCode;
	    }
	    
	    public int code(){
	    	return _myCode;
	    }
	    
	    public static CCKeyCode valueForCode(int theKeyCode){
	    	switch(theKeyCode){
	    	case 10 : return VK_ENTER;
	    	case 8 : return VK_BACK_SPACE;
	    	case 9 : return VK_TAB;
	    	case 3 : return VK_CANCEL;
	    	case 12 : return VK_CLEAR;
	    	case 16 : return VK_SHIFT;
	    	case 17 : return VK_CONTROL;
	    	case 18 : return VK_ALT;
	    	case 19 : return VK_PAUSE;
	    	case 20 : return VK_CAPS_LOCK;
	    	case 27 : return VK_ESCAPE;
	    	case 32 : return VK_SPACE;
	    	case 33 : return VK_PAGE_UP;
	    	case 34 : return VK_PAGE_DOWN;
	    	case 35 : return VK_END;
	    	case 36 : return VK_HOME;
	    	case 37 : return VK_LEFT;
	    	case 38 : return VK_UP;
	    	case 39 : return VK_RIGHT;
	    	case 40 : return VK_DOWN;
	    	case 44 : return VK_COMMA;
	    	case 45 : return VK_MINUS;
	    	case 46 : return VK_PERIOD;
	    	case 47 : return VK_SLASH;
	    	case 48 : return VK_0;
	    	case 49 : return VK_1;
	    	case 50 : return VK_2;
	    	case 51 : return VK_3;
	    	case 52 : return VK_4;
	    	case 53 : return VK_5;
	    	case 54 : return VK_6;
	    	case 55 : return VK_7;
	    	case 56 : return VK_8;
	    	case 57 : return VK_9;
	    	case 59 : return VK_SEMICOLON;
	    	case 61 : return VK_EQUALS;
	    	case 65 : return VK_A;
	    	case 66 : return VK_B;
	    	case 67 : return VK_C;
	    	case 68 : return VK_D;
	    	case 69 : return VK_E;
	    	case 70 : return VK_F;
	    	case 71 : return VK_G;
	    	case 72 : return VK_H;
	    	case 73 : return VK_I;
	    	case 74 : return VK_J;
	    	case 75 : return VK_K;
	    	case 76 : return VK_L;
	    	case 77 : return VK_M;
	    	case 78 : return VK_N;
	    	case 79 : return VK_O;
	    	case 80 : return VK_P;
	    	case 81 : return VK_Q;
	    	case 82 : return VK_R;
	    	case 83 : return VK_S;
	    	case 84 : return VK_T;
	    	case 85 : return VK_U;
	    	case 86 : return VK_V;
	    	case 87 : return VK_W;
	    	case 88 : return VK_X;
	    	case 89 : return VK_Y;
	    	case 90 : return VK_Z;
	    	case 91 : return VK_OPEN_BRACKET;
	    	case 92 : return VK_BACK_SLASH;
	    	case 93 : return VK_CLOSE_BRACKET;
	    	case 96 : return VK_NUMPAD0;
	    	case 97 : return VK_NUMPAD1;
	    	case 98 : return VK_NUMPAD2;
	    	case 99 : return VK_NUMPAD3;
	    	case 100 : return VK_NUMPAD4;
	    	case 101 : return VK_NUMPAD5;
	    	case 102 : return VK_NUMPAD6;
	    	case 103 : return VK_NUMPAD7;
	    	case 104 : return VK_NUMPAD8;
	    	case 105 : return VK_NUMPAD9;
	    	case 106 : return VK_MULTIPLY;
	    	case 107 : return VK_ADD;
	    	case 108 : return VK_SEPARATOR;
	    	case 109 : return VK_SUBTRACT;
	    	case 110 : return VK_DECIMAL;
	    	case 111 : return VK_DIVIDE;
	    	case 127 : return VK_DELETE;
	    	case 144 : return VK_NUM_LOCK;
	    	case 145 : return VK_SCROLL_LOCK;
	    	case 112 : return VK_F1;
	    	case 113 : return VK_F2;
	    	case 114 : return VK_F3;
	    	case 115 : return VK_F4;
	    	case 116 : return VK_F5;
	    	case 117 : return VK_F6;
	    	case 118 : return VK_F7;
	    	case 119 : return VK_F8;
	    	case 120 : return VK_F9;
	    	case 121 : return VK_F10;
	    	case 122 : return VK_F11;
	    	case 123 : return VK_F12;
	    	case 61440 : return VK_F13;
	    	case 61441 : return VK_F14;
	    	case 61442 : return VK_F15;
	    	case 61443 : return VK_F16;
	    	case 61444 : return VK_F17;
	    	case 61445 : return VK_F18;
	    	case 61446 : return VK_F19;
	    	case 61447 : return VK_F20;
	    	case 61448 : return VK_F21;
	    	case 61449 : return VK_F22;
	    	case 61450 : return VK_F23;
	    	case 61451 : return VK_F24;
	    	case 154 : return VK_PRINTSCREEN;
	    	case 155 : return VK_INSERT;
	    	case 156 : return VK_HELP;
	    	case 157 : return VK_META;
	    	case 192 : return VK_BACK_QUOTE;
	    	case 222 : return VK_QUOTE;
	    	case 224 : return VK_KP_UP;
	    	case 225 : return VK_KP_DOWN;
	    	case 226 : return VK_KP_LEFT;
	    	case 227 : return VK_KP_RIGHT;
	    	case 128 : return VK_DEAD_GRAVE;
	    	case 129 : return VK_DEAD_ACUTE;
	    	case 130 : return VK_DEAD_CIRCUMFLEX;
	    	case 131 : return VK_DEAD_TILDE;
	    	case 132 : return VK_DEAD_MACRON;
	    	case 133 : return VK_DEAD_BREVE;
	    	case 134 : return VK_DEAD_ABOVEDOT;
	    	case 135 : return VK_DEAD_DIAERESIS;
	    	case 136 : return VK_DEAD_ABOVERING;
	    	case 137 : return VK_DEAD_DOUBLEACUTE;
	    	case 138 : return VK_DEAD_CARON;
	    	case 139 : return VK_DEAD_CEDILLA;
	    	case 140 : return VK_DEAD_OGONEK;
	    	case 141 : return VK_DEAD_IOTA;
	    	case 142 : return VK_DEAD_VOICED_SOUND;
	    	case 143 : return VK_DEAD_SEMIVOICED_SOUND;
	    	case 150 : return VK_AMPERSAND;
	    	case 151 : return VK_ASTERISK;
	    	case 152 : return VK_QUOTEDBL;
	    	case 153 : return VK_LESS;
	    	case 160 : return VK_GREATER;
	    	case 161 : return VK_BRACELEFT;
	    	case 162 : return VK_BRACERIGHT;
	    	case 512 : return VK_AT;
	    	case 513 : return VK_COLON;
	    	case 514 : return VK_CIRCUMFLEX;
	    	case 515 : return VK_DOLLAR;
	    	case 516 : return VK_EURO_SIGN;
	    	case 517 : return VK_EXCLAMATION_MARK;
	    	case 518 : return VK_INVERTED_EXCLAMATION_MARK;
	    	case 519 : return VK_LEFT_PARENTHESIS;
	    	case 520 : return VK_NUMBER_SIGN;
	    	case 521 : return VK_PLUS;
	    	case 522 : return VK_RIGHT_PARENTHESIS;
	    	case 523 : return VK_UNDERSCORE;
	    	case 524 : return VK_WINDOWS;
	    	case 525 : return VK_CONTEXT_MENU;
	    	case 24 : return VK_FINAL;
	    	case 28 : return VK_CONVERT;
	    	case 29 : return VK_NONCONVERT;
	    	case 30 : return VK_ACCEPT;
	    	case 31 : return VK_MODECHANGE;
	    	case 21 : return VK_KANA;
	    	case 25 : return VK_KANJI;
	    	case 240 : return VK_ALPHANUMERIC;
	    	case 241 : return VK_KATAKANA;
	    	case 242 : return VK_HIRAGANA;
	    	case 243 : return VK_FULL_WIDTH;
	    	case 244 : return VK_HALF_WIDTH;
	    	case 245 : return VK_ROMAN_CHARACTERS;
	    	case 256 : return VK_ALL_CANDIDATES;
	    	case 257 : return VK_PREVIOUS_CANDIDATE;
	    	case 258 : return VK_CODE_INPUT;
	    	case 259 : return VK_JAPANESE_KATAKANA;
	    	case 260 : return VK_JAPANESE_HIRAGANA;
	    	case 261 : return VK_JAPANESE_ROMAN;
	    	case 262 : return VK_KANA_LOCK;
	    	case 263 : return VK_INPUT_METHOD_ON_OFF;
	    	case 65489 : return VK_CUT;
	    	case 65485 : return VK_COPY;
	    	case 65487 : return VK_PASTE;
	    	case 65483 : return VK_UNDO;
	    	case 65481 : return VK_AGAIN;
	    	case 65488 : return VK_FIND;
	    	case 65482 : return VK_PROPS;
	    	case 65480 : return VK_STOP;
	    	case 65312 : return VK_COMPOSE;
	    	case 65406 : return VK_ALT_GRAPH;
	    	case 65368 : return VK_BEGIN;
	    	case 0 : return VK_UNDEFINED;
	    	default : return VK_UNDEFINED;
	    	}
	    }
	}
	

	
	private CCKeyEventType _myType;
	private char _myChar;
	private CCKeyCode _myCode;
	private int _myLocation;
	
	private boolean _myIsAltDown;
	private boolean _myIsAltGraphDown;
	private boolean _myIsControlDown;
	private boolean _myIsMetaDown;
	private boolean _myIsShiftDown;

	/**
	 * @invisible
	 * @param theEvent
	 */
	public CCKeyEvent(final java.awt.event.KeyEvent theEvent) {
		super(KEY_EVENT);
		
		switch(theEvent.getID()){
		case java.awt.event.KeyEvent.KEY_PRESSED:
			_myType = CCKeyEventType.PRESSED;
			break;
		case java.awt.event.KeyEvent.KEY_RELEASED:
			_myType = CCKeyEventType.RELEASED;
			break;
		case java.awt.event.KeyEvent.KEY_TYPED:
			_myType = CCKeyEventType.TYPED;
			break;
		}
		
		_myChar = theEvent.getKeyChar();
		_myCode = CCKeyCode.valueForCode(theEvent.getKeyCode());
		_myLocation = theEvent.getKeyLocation();
		
		_myIsAltDown = theEvent.isAltDown();
		_myIsAltGraphDown = theEvent.isAltGraphDown();
		_myIsControlDown = theEvent.isControlDown();
		_myIsMetaDown = theEvent.isMetaDown();
		_myIsShiftDown = theEvent.isShiftDown();
//		super(
//			(Component) theEvent.getSource(), 
//			theEvent.getID(), 
//			theEvent.getWhen(), 
//			theEvent.getModifiers(), 
//			theEvent.getKeyCode(), 
//			theEvent.getKeyChar(), 
//			theEvent.getKeyLocation());
	}
	
	public CCKeyEvent(final com.jogamp.newt.event.KeyEvent theEvent, CCKeyEventType theType) {
		super(KEY_EVENT);
		_myType = theType;
		_myChar = theEvent.getKeyChar();
		_myCode = CCKeyCode.valueForCode(theEvent.getKeyCode());
		
		_myIsAltDown = theEvent.isAltDown();
		_myIsAltGraphDown = theEvent.isAltGraphDown();
		_myIsControlDown = theEvent.isControlDown();
		_myIsMetaDown = theEvent.isMetaDown();
		_myIsShiftDown = theEvent.isShiftDown();
//		super(
//			(Component) theEvent.getSource(), 
//			theEvent.getID(), 
//			theEvent.getWhen(), 
//			theEvent.getModifiers(), 
//			theEvent.getKeyCode(), 
//			theEvent.getKeyChar(), 
//			theEvent.getKeyLocation());
	}
	
	public CCKeyEventType type(){
		return _myType;
	}

	/**
	 * Returns the character associated with the key in this event.
	 * For example, on key typed for shift + "a" 
	 * returns the value for "A".
	 * <p>
	 * Key pressed and key released events 
	 * are not intended for reporting of character input.  Therefore, 
	 * the values returned by this method are guaranteed to be 
	 * meaningful only for key typed events.  
	 *
	 * @return the Unicode character defined for this key event.
	 *         If no valid Unicode character exists for this key event, 
	 *         <code>CHAR_UNDEFINED</code> is returned.
	 */
	public char keyChar() {
		return _myChar;
	}

	/**
	 * Returns the integer keyCode associated with the key in this event.
	 * 
	 * @return the integer code for an actual key on the keyboard. 
	 *         (For key typed events, the keyCode is <code>VK_UNDEFINED</code>.)
	 * @see #keyChar()
	 * @see #keyLocation()
	 */
	public CCKeyCode keyCode() {
		return _myCode;
	}

	/**
	 * Returns the location of the key that originated this key event.
	 *
	 * Some keys occur more than once on a keyboard, e.g. the left and
	 * right shift keys.  Additionally, some keys occur on the numeric
	 * keypad.  This provides a way of distinguishing such keys.
	 *
	 * @return the location of the key that was pressed or released.
	 *         Always returns <code>KEY_LOCATION_UNKNOWN</code> for 
	 *         key typed events.
	 * @see #keyChar()
	 * @see #keyCode()
	 */
	public int keyLocation() {
		return _myLocation;
	}

	/**
     * Returns whether or not the Shift modifier is down on this event.
     */
    public boolean isShiftDown() {
        return _myIsShiftDown;
    }

    /**
     * Returns whether or not the Control modifier is down on this event.
     */
    public boolean isControlDown() {
        return _myIsControlDown;
    }

    /**
     * Returns whether or not the Meta modifier is down on this event.
     */
    public boolean isMetaDown() {
        return _myIsMetaDown;
    }

    /**
     * Returns whether or not the Alt modifier is down on this event.
     */
    public boolean isAltDown() {
        return _myIsAltDown;
    }

    /**
     * Returns whether or not the AltGraph modifier is down on this event.
     */
    public boolean isAltGraphDown() {
        return _myIsAltGraphDown;
    }
}
