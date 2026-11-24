package com.logandhillon.logangamelib.api.input;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum of every key code for input handling.
 *
 * @author Logan Dhillon
 */
@ToString
public enum KeyCode {
    ENTER(0x0A, "Enter", Type.WHITESPACE),

    BACKSPACE(0x08, "Backspace"),

    TAB(0x09, "Tab", Type.WHITESPACE),

    CANCEL(0x03, "Cancel"),

    CLEAR(0x0C, "Clear"),

    SHIFT(0x10, "Shift", Type.MODIFIER),

    CONTROL(0x11, "Ctrl", Type.MODIFIER),

    ALT(0x12, "Alt", Type.MODIFIER),

    /**
     * macOS command key
     */
    COMMAND(0x300, "Command", Type.MODIFIER),

    PAUSE(0x13, "Pause"),

    CAPS(0x14, "Caps Lock"),

    ESCAPE(0x1B, "Esc"),

    SPACE(0x20, "Space", Type.WHITESPACE),

    PAGE_UP(0x21, "Page Up", Type.NAVIGATION),

    PAGE_DOWN(0x22, "Page Down", Type.NAVIGATION),

    END(0x23, "End", Type.NAVIGATION),

    HOME(0x24, "Home", Type.NAVIGATION),

    LEFT(0x25, "Left", Type.ARROW | Type.NAVIGATION),

    UP(0x26, "Up", Type.ARROW | Type.NAVIGATION),

    RIGHT(0x27, "Right", Type.ARROW | Type.NAVIGATION),

    DOWN(0x28, "Down", Type.ARROW | Type.NAVIGATION),

    COMMA(0x2C, ","),

    MINUS(0x2D, "-"),

    PERIOD(0x2E, "."),

    SLASH(0x2F, "/"),

    NUM_0(0x30, "0", Type.DIGIT),

    NUM_1(0x31, "1", Type.DIGIT),

    NUM_2(0x32, "2", Type.DIGIT),

    NUM_3(0x33, "3", Type.DIGIT),

    NUM_4(0x34, "4", Type.DIGIT),

    NUM_5(0x35, "5", Type.DIGIT),

    NUM_6(0x36, "6", Type.DIGIT),

    NUM_7(0x37, "7", Type.DIGIT),

    NUM_8(0x38, "8", Type.DIGIT),

    NUM_9(0x39, "9", Type.DIGIT),

    SEMICOLON(0x3B, ";"),

    EQUALS(0x3D, "="),

    A(0x41, "A", Type.LETTER),

    B(0x42, "B", Type.LETTER),

    C(0x43, "C", Type.LETTER),

    D(0x44, "D", Type.LETTER),

    E(0x45, "E", Type.LETTER),

    F(0x46, "F", Type.LETTER),

    G(0x47, "G", Type.LETTER),

    H(0x48, "H", Type.LETTER),

    I(0x49, "I", Type.LETTER),

    J(0x4A, "J", Type.LETTER),

    K(0x4B, "K", Type.LETTER),

    L(0x4C, "L", Type.LETTER),

    M(0x4D, "M", Type.LETTER),

    N(0x4E, "N", Type.LETTER),

    O(0x4F, "O", Type.LETTER),

    P(0x50, "P", Type.LETTER),

    Q(0x51, "Q", Type.LETTER),

    R(0x52, "R", Type.LETTER),

    S(0x53, "S", Type.LETTER),

    T(0x54, "T", Type.LETTER),

    U(0x55, "U", Type.LETTER),

    V(0x56, "V", Type.LETTER),

    W(0x57, "W", Type.LETTER),

    X(0x58, "X", Type.LETTER),

    Y(0x59, "Y", Type.LETTER),

    Z(0x5A, "Z", Type.LETTER),

    BACK_SLASH(0x5C, "\\"),

    LEFT_BRACKET(0x5B, "["),

    RIGHT_BRACKET(0x5D, "]"),

    NUMPAD0(0x60, "Numpad 0", Type.DIGIT | Type.KEYPAD),

    NUMPAD1(0x61, "Numpad 1", Type.DIGIT | Type.KEYPAD),

    NUMPAD2(0x62, "Numpad 2", Type.DIGIT | Type.KEYPAD),

    NUMPAD3(0x63, "Numpad 3", Type.DIGIT | Type.KEYPAD),

    NUMPAD4(0x64, "Numpad 4", Type.DIGIT | Type.KEYPAD),

    NUMPAD5(0x65, "Numpad 5", Type.DIGIT | Type.KEYPAD),

    NUMPAD6(0x66, "Numpad 6", Type.DIGIT | Type.KEYPAD),

    NUMPAD7(0x67, "Numpad 7", Type.DIGIT | Type.KEYPAD),

    NUMPAD8(0x68, "Numpad 8", Type.DIGIT | Type.KEYPAD),

    NUMPAD9(0x69, "Numpad 9", Type.DIGIT | Type.KEYPAD),

    MULTIPLY(0x6A, "Multiply"),

    ADD(0x6B, "Add"),

    SEPARATOR(0x6C, "Seperator"),

    SUBTRACT(0x6D, "Subtract"),

    DECIMAL(0x6E, "Decimal"),

    DIVIDE(0x6F, "Divide"),

    DELETE(0x7F, "Delete"),

    NUM_LOCK(0x90, "Num Lock"),

    SCROLL_LOCK(0x91, "Scroll Lock"),

    F1(0x70, "F1", Type.FUNCTION),

    F2(0x71, "F2", Type.FUNCTION),

    F3(0x72, "F3", Type.FUNCTION),

    F4(0x73, "F4", Type.FUNCTION),

    F5(0x74, "F5", Type.FUNCTION),

    F6(0x75, "F6", Type.FUNCTION),

    F7(0x76, "F7", Type.FUNCTION),

    F8(0x77, "F8", Type.FUNCTION),

    F9(0x78, "F9", Type.FUNCTION),

    F10(0x79, "F10", Type.FUNCTION),

    F11(0x7A, "F11", Type.FUNCTION),

    F12(0x7B, "F12", Type.FUNCTION),

    F13(0xF000, "F13", Type.FUNCTION),

    F14(0xF001, "F14", Type.FUNCTION),

    F15(0xF002, "F15", Type.FUNCTION),

    F16(0xF003, "F16", Type.FUNCTION),

    F17(0xF004, "F17", Type.FUNCTION),

    F18(0xF005, "F18", Type.FUNCTION),

    F19(0xF006, "F19", Type.FUNCTION),

    F20(0xF007, "F20", Type.FUNCTION),

    F21(0xF008, "F21", Type.FUNCTION),

    F22(0xF009, "F22", Type.FUNCTION),

    F23(0xF00A, "F23", Type.FUNCTION),

    F24(0xF00B, "F24", Type.FUNCTION),

    PRINT_SCREEN(0x9A, "Print Screen"),

    INSERT(0x9B, "Ins"),

    META(0x9D, "Meta", Type.MODIFIER),

    BACK_QUOTE(0xC0, "`"),

    APOSTROPHE(0xDE, "'"),

    NUMPAD_UP(0xE0, "Numpad Up", Type.ARROW | Type.NAVIGATION | Type.KEYPAD),

    NUMPAD_DOWN(0xE1, "Numpad Down", Type.ARROW | Type.NAVIGATION | Type.KEYPAD),

    NUMPAD_LEFT(0xE2, "Numpad Left", Type.ARROW | Type.NAVIGATION | Type.KEYPAD),

    NUMPAD_RIGHT(0xE3, "Numpad Right", Type.ARROW | Type.NAVIGATION | Type.KEYPAD),

    AMPERSAND(0x96, "&"),

    ASTERISK(0x97, "*"),

    QUOTATION_MARKS(0x98, "\""),

    LESS(0x99, "<"),

    GREATER(0xa0, ">"),

    BRACE_LEFT(0xa1, "{"),

    BRACE_RIGHT(0xa2, "}"),

    AT(0x0200, "@"),

    COLON(0x0201, ":"),

    DOLLAR(0x0203, "$"),

    EXCLAMATION_MARK(0x0205, "!"),

    INVERTED_EXCLAMATION_MARK(0x0206, "¡"),

    LEFT_PARENTHESIS(0x0207, "("),

    NUMBER_SIGN(0x0208, "#"),

    PLUS(0x0209, "+"),

    RIGHT_PARENTHESIS(0x020A, ")"),

    UNDERSCORE(0x020B, "_"),

    WINDOWS(0x020C, "Windows", Type.MODIFIER),

    CONTEXT_MENU(0x020D, "Context Menu"),

    /**
     * used if keycode is unknown
     */
    UNDEFINED(0x0, "Undefined");

    final int    code;
    final String ch;
    final String name;

    private final int mask;

    private static class Type {
        private static final int FUNCTION   = 1;
        private static final int NAVIGATION = 1 << 1;
        private static final int ARROW      = 1 << 2;
        private static final int MODIFIER   = 1 << 3;
        private static final int LETTER     = 1 << 4;
        private static final int DIGIT      = 1 << 5;
        private static final int KEYPAD     = 1 << 6;
        private static final int WHITESPACE = 1 << 7;
    }

    KeyCode(int code, String name, int mask) {
        this.code = code; this.name = name; this.mask = mask;
        // ch = new String(Character.toChars(code));
        ch = String.valueOf((char)code);
    }

    KeyCode(int code, String name) {
        this(code, name, 0);
    }

    /**
     * Function keys like F1, F2, etc...
     */
    public final boolean isFunctionKey() {
        return (mask & Type.FUNCTION) != 0;
    }

    /**
     * Navigation keys are arrow keys and Page Down, Page Up, Home, End (including keypad keys)
     */
    public final boolean isNavigationKey() {
        return (mask & Type.NAVIGATION) != 0;
    }

    /**
     * Left, right, up, down keys (including the keypad arrows)
     */
    public final boolean isArrowKey() {
        return (mask & Type.ARROW) != 0;
    }

    /**
     * Keys that could act as a modifier
     */
    public final boolean isModifierKey() {
        return (mask & Type.MODIFIER) != 0;
    }

    /**
     * All keys with letters
     */
    public final boolean isLetterKey() {
        return (mask & Type.LETTER) != 0;
    }

    /**
     * All Digit keys (including the keypad digits)
     */
    public final boolean isDigitKey() {
        return (mask & Type.DIGIT) != 0;
    }

    /**
     * All keys on the keypad
     */
    public final boolean isKeypadKey() {
        return (mask & Type.KEYPAD) != 0;
    }

    /**
     * Space, tab and enter
     */
    public final boolean isWhitespaceKey() {
        return (mask & Type.WHITESPACE) != 0;
    }

    /**
     * @return keycode name in a printable format
     */
    public final String getPrintableName() {
        return name;
    }

    /**
     * Maps the {@link getCode OS keycode} to a printable character
     *
     * @return key code character
     */
    public final String getChar() {
        return ch;
    }

    /**
     * @return OS-level keycode
     */
    public final int getCode() {
        return code;
    }

    private static final Map<Integer, KeyCode> lookup;

    static {
        lookup = new HashMap<>(KeyCode.values().length);
        for (KeyCode c: KeyCode.values()) {
            lookup.put(c.code, c);
        }
    }

    /**
     * Parses a keycode's scancode into a {@link KeyCode}
     *
     * @param code scancode, i.e. from {@link getCode}
     *
     * @return KeyCode with corresponding scancode, otherwise null
     */
    public static KeyCode of(int code) {
        return lookup.getOrDefault(code, UNDEFINED);
    }

    /**
     * Record for possible keyboard modifiers on a keypress.
     */
    public record Modifiers(
            boolean shift,
            boolean control,
            boolean alt,
            boolean meta
    ) {}
}

