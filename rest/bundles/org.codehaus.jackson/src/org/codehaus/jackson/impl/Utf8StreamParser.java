package org.codehaus.jackson.impl;

import static org.codehaus.jackson.JsonReadContext.HANDLED_EXPECT_NAME;
import static org.codehaus.jackson.JsonReadContext.HANDLED_EXPECT_VALUE;
import static org.codehaus.jackson.JsonReadContext.MISSING_COLON;
import static org.codehaus.jackson.JsonReadContext.MISSING_COMMA;
import static org.codehaus.jackson.JsonReadContext.NOT_EXP_SEPARATOR_NEED_NAME;
import static org.codehaus.jackson.JsonReadContext.NOT_EXP_SEPARATOR_NEED_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.io.IOContext;
import org.codehaus.jackson.sym.Name;
import org.codehaus.jackson.sym.NameCanonicalizer;
import org.codehaus.jackson.util.CharTypes;

/**
 * This is a concrete implementation of {@link JsonParser}, which is
 * based on a {@link java.io.InputStream} as the input source.
 */
public final class Utf8StreamParser
    extends Utf8NumericParser
{
    /*
    ////////////////////////////////////////////////////
    // Configuration
    ////////////////////////////////////////////////////
    */

    final protected NameCanonicalizer mSymbols;

    /**
     * This buffer is used for name parsing.
     */
    protected int[] mQuadBuffer = new int[32];

    /*
    ////////////////////////////////////////////////////
    // Life-cycle
    ////////////////////////////////////////////////////
     */

    public Utf8StreamParser(IOContext ctxt, InputStream in,
                            NameCanonicalizer sym,
                            byte[] inputBuffer, int start, int end,
                            boolean bufferRecyclable)
    {
        super(ctxt, in, inputBuffer, start, end, bufferRecyclable);
        mSymbols = sym;
    }

    /*
    ////////////////////////////////////////////////////
    // Public API, traversal
    ////////////////////////////////////////////////////
     */

    /**
     * @return Next token from the stream, if any found, or null
     *   to indicate end-of-input
     */
    public JsonToken nextToken()
        throws IOException, JsonParseException
    {
        if (mTokenIncomplete) {
            mTokenIncomplete = false; // only strings can be partial
            skipString();
        }

        int i;

        // Space to skip?
        while (true) {
            if (mInputPtr >= mInputLast) {
                if (!loadMore()) {
                    handleEOF();
                    return (mCurrToken = null);
                }
            }
            i = mInputBuffer[mInputPtr++] & 0xFF;
            if (i > INT_SPACE) {
                break;
            }
            if (i != INT_SPACE) {
                if (i == INT_LF) {
                    skipLF();
                } else if (i == INT_CR) {
                    skipCR();
                } else if (i != INT_TAB) {
                    throwInvalidSpace(i);
                }
            }
        }

        /* First, need to ensure we know the starting location of token
         * after skipping leading white space
         */
        mTokenInputTotal = mCurrInputProcessed + mInputPtr - 1;
        mTokenInputRow = mCurrInputRow;
        mTokenInputCol = mInputPtr - mCurrInputRowStart - 1;

        // Closing scope?
        if (i == INT_RBRACKET) {
            if (!mParsingContext.isArray()) {
                reportMismatchedEndMarker(i, ']');
            }
            mParsingContext = mParsingContext.getParent();
            return (mCurrToken = JsonToken.END_ARRAY);
        }
        if (i == INT_RCURLY) {
            if (!mParsingContext.isObject()) {
                reportMismatchedEndMarker(i, '}');
            }
            mParsingContext = mParsingContext.getParent();
            return (mCurrToken = JsonToken.END_OBJECT);
        }

        // Nope. Have and/or need a separator?
        int sep = mParsingContext.handleSeparator(i);

        switch (sep) {
        case HANDLED_EXPECT_NAME:
        case HANDLED_EXPECT_VALUE:
            // Need to skip space, find next char
            while (true) {
                if (mInputPtr >= mInputLast) {
                    if (!loadMore()) {
                        reportError("Unexpected end-of-input within/between "+mParsingContext.getTypeDesc()+" entries");
                    }
                }
                i = mInputBuffer[mInputPtr++] & 0xFF;
                if (i > INT_SPACE) {
                    break;
                }
                if (i != INT_SPACE) {
                    if (i == INT_LF) {
                        skipLF();
                    } else if (i == INT_CR) {
                        skipCR();
                    } else if (i != INT_TAB) {
                        throwInvalidSpace(i);
                    }
                }
            }
            // And if we expect a name, must be quote
            if (sep == HANDLED_EXPECT_NAME) {
                mParsingContext.setCurrentName(parseFieldName(i).getName());
                return (mCurrToken = JsonToken.FIELD_NAME);
            }
            break;
        case MISSING_COMMA:
            reportUnexpectedChar(i, "was expecting comma to separate "+mParsingContext.getTypeDesc()+" entries");
        case MISSING_COLON:
            reportUnexpectedChar(i, "was expecting colon to separate field name and value");
        case NOT_EXP_SEPARATOR_NEED_VALUE:
            break;
        case NOT_EXP_SEPARATOR_NEED_NAME:
            mParsingContext.setCurrentName(parseFieldName(i).getName());
            return (mCurrToken = JsonToken.FIELD_NAME);
        }

        // We now have the first char: what did we get?
        switch (i) {
        case INT_QUOTE:
            mTokenIncomplete = true;
            return (mCurrToken = JsonToken.VALUE_STRING);
        case INT_LBRACKET:
            //mParsingContext = mParsingContext.createChildArrayContext(this);
            mParsingContext = mParsingContext.createChildArrayContext(mTokenInputRow, mTokenInputCol);
            return (mCurrToken = JsonToken.START_ARRAY);
        case INT_LCURLY:
            //mParsingContext = mParsingContext.createChildObjectContext(this);
            mParsingContext = mParsingContext.createChildObjectContext(mTokenInputRow, mTokenInputCol);
            return (mCurrToken = JsonToken.START_OBJECT);
        case INT_RBRACKET:
        case INT_RCURLY:
            // Error: neither is valid at this point; valid closers have
            // been handled earlier
            reportUnexpectedChar(i, "expected a value");
        case INT_t:
            return matchToken(JsonToken.VALUE_TRUE);
        case INT_f:
            return matchToken(JsonToken.VALUE_FALSE);
        case INT_n:
            return matchToken(JsonToken.VALUE_NULL);

        case INT_MINUS:
            /* Should we have separate handling for plus? Although
             * it is not allowed per se, it may be erroneously used,
             * and could be indicate by a more specific error message.
             */
        case INT_0:
        case INT_1:
        case INT_2:
        case INT_3:
        case INT_4:
        case INT_5:
        case INT_6:
        case INT_7:
        case INT_8:
        case INT_9:
            return parseNumberText(i);
        }
        reportUnexpectedChar(i, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
        return null; // never gets here
    }
        
    @Override
    public void close()
        throws IOException
    {
        super.close();
        // Merge found symbols, if any:
        mSymbols.release();
    }

    /*
    ////////////////////////////////////////////////////
    // Public API, binary access
    ////////////////////////////////////////////////////
     */

    @Override
    public int readBinaryValue(OutputStream results)
        throws IOException, JsonParseException
    {
        // !!! TBI: implemented base64 decoding
        return -1;
    }

    /*
    ////////////////////////////////////////////////////
    // Internal methods, secondary parsing
    ////////////////////////////////////////////////////
     */

    protected Name parseFieldName(int i)
        throws IOException, JsonParseException
    {
        if (i != INT_QUOTE) {
            reportUnexpectedChar(i, "was expecting double-quote to start field name");
        }
        // We'll never parse name String straight into buffer.
        mFieldInBuffer = false; // by default let's expect it won't get there

        // First: can we optimize out bounds checks?
        if ((mInputLast - mInputPtr) < 9) { // Need 8 chars, plus one trailing (quote)
            return slowParseFieldName();
        }

        // If so, can also unroll loops nicely
        final int[] codes = CharTypes.getInputCode();

        int q = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[q] != 0) {
            if (q == INT_QUOTE) { // special case, ""
                return NameCanonicalizer.getEmptyName();
            }
            return parseFieldName(0, q, 1); // quoting or invalid char
        }

        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // one byte/char case or broken
                return findName(q, 1);
            }
            return parseFieldName(q, i, 1);
        }
        q = (q << 8) | i;
        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // two byte name or broken
                return findName(q, 2);
            }
            return parseFieldName(q, i, 2);
        }
        q = (q << 8) | i;
        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // three byte name or broken
                return findName(q, 3);
            }
            return parseFieldName(q, i, 3);
        }
        q = (q << 8) | i;
        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // four byte name or broken
                return findName(q, 4);
            }
            return parseFieldName(q, i, 4);
        }
        return parseMediumFieldName(q, i);
    }

    protected Name parseMediumFieldName(int q1, int q2)
        throws IOException, JsonParseException
    {
        final int[] codes = CharTypes.getInputCode();

        // Ok, got 5 name bytes so far
        int i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 5 bytes
                return findName(q1, q2, 1);
            }
            return parseFieldName(q1, q2, i, 1); // quoting or invalid char
        }
        q2 = (q2 << 8) | i;
        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 6 bytes
                return findName(q1, q2, 2);
            }
            return parseFieldName(q1, q2, i, 2);
        }
        q2 = (q2 << 8) | i;
        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 7 bytes
                return findName(q1, q2, 3);
            }
            return parseFieldName(q1, q2, i, 3);
        }
        q2 = (q2 << 8) | i;
        i = mInputBuffer[mInputPtr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 8 bytes
                return findName(q1, q2, 4);
            }
            return parseFieldName(q1, q2, i, 4);
        }
        mQuadBuffer[0] = q1;
        mQuadBuffer[1] = q2;
        return parseLongFieldName(i);
    }

    protected Name parseLongFieldName(int q)
        throws IOException, JsonParseException
    {
        final int[] codes = CharTypes.getInputCode();
        int qlen = 2;

        while (true) {
            /* Let's offline if we hit buffer boundary (otherwise would
             * need to [try to] align input, which is bit complicated
             * and may not always be possible)
             */
            if ((mInputLast - mInputPtr) < 4) {
                return parseEscapedFieldName(mQuadBuffer, qlen, 0, q, 0);
            }
            // Otherwise can skip boundary checks for 4 bytes in loop

            int i = mInputBuffer[mInputPtr++] & 0xFF;
            if (codes[i] != 0) {
                if (i == INT_QUOTE) {
                    return findName(mQuadBuffer, qlen, q, 1);
                }
                return parseEscapedFieldName(mQuadBuffer, qlen, q, i, 1);
            }

            q = (q << 8) | i;
            i = mInputBuffer[mInputPtr++] & 0xFF;
            if (codes[i] != 0) {
                if (i == INT_QUOTE) {
                    return findName(mQuadBuffer, qlen, q, 2);
                }
                return parseEscapedFieldName(mQuadBuffer, qlen, q, i, 2);
            }

            q = (q << 8) | i;
            i = mInputBuffer[mInputPtr++] & 0xFF;
            if (codes[i] != 0) {
                if (i == INT_QUOTE) {
                    return findName(mQuadBuffer, qlen, q, 3);
                }
                return parseEscapedFieldName(mQuadBuffer, qlen, q, i, 3);
            }

            q = (q << 8) | i;
            i = mInputBuffer[mInputPtr++] & 0xFF;
            if (codes[i] != 0) {
                if (i == INT_QUOTE) {
                    return findName(mQuadBuffer, qlen, q, 4);
                }
                return parseEscapedFieldName(mQuadBuffer, qlen, q, i, 4);
            }

            // Nope, no end in sight. Need to grow quad array etc
            if (qlen >= mQuadBuffer.length) {
                mQuadBuffer = growArrayBy(mQuadBuffer, qlen);
            }
            mQuadBuffer[qlen++] = q;
            q = i;
        }
    }

    /**
     * Method called when not even first 8 bytes are guaranteed
     * to come consequtively. Happens rarely, so this is offlined;
     * plus we'll also do full checks for escaping etc.
     */
    protected Name slowParseFieldName()
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            if (!loadMore()) {
                reportInvalidEOF(": was expecting closing quote for name");
            }
        }
        int i = mInputBuffer[mInputPtr++] & 0xFF;
        if (i == INT_QUOTE) { // special case, ""
            return NameCanonicalizer.getEmptyName();
        }
        return parseEscapedFieldName(mQuadBuffer, 0, 0, i, 0);
    }

    private final Name parseFieldName(int q1, int ch, int lastQuadBytes)
        throws IOException, JsonParseException
    {
        return parseEscapedFieldName(mQuadBuffer, 0, q1, ch, lastQuadBytes);
    }

    private final Name parseFieldName(int q1, int q2, int ch, int lastQuadBytes)
        throws IOException, JsonParseException
    {
        mQuadBuffer[0] = q1;
        return parseEscapedFieldName(mQuadBuffer, 1, q2, ch, lastQuadBytes);
    }

    /**
     * Slower parsing method which is generally branched to when
     * an escape sequence is detected (or alternatively for long
     * names, or ones crossing input buffer boundary). In any case,
     * needs to be able to handle more exceptional cases, gets
     * slower, and hance is offlined to a separate method.
     */
    protected Name parseEscapedFieldName(int[] quads, int qlen, int currQuad, int ch,
                                         int currQuadBytes)
        throws IOException, JsonParseException
    {
        final int[] codes = CharTypes.getInputCode();

        while (true) {
            if (codes[ch] != 0) {
                if (ch == INT_QUOTE) { // we are done
                    break;
                }
                // Unquoted white space?
                if (ch != INT_BACKSLASH) {
                    throwUnquotedSpace(ch, "name");
                }

                // Nope, escape sequence

                ch = decodeEscaped();
                /* Oh crap. May need to UTF-8 (re-)encode it, if it's
                 * beyond 7-bit ascii. Gets pretty messy.
                 * If this happens often, may want to use different name
                 * canonicalization to avoid these hits.
                 */
                if (ch > 127) {
                    // Ok, we'll need room for first byte right away
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            mQuadBuffer = quads = growArrayBy(quads, quads.length);
                        }
                        quads[qlen++] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    if (ch < 0x800) { // 2-byte
                        currQuad = (currQuad << 8) | (0xc0 | (ch >> 6));
                        ++currQuadBytes;
                        // Second byte gets output below:
                    } else { // 3 bytes; no need to worry about surrogates here
                        currQuad = (currQuad << 8) | (0xe0 | (ch >> 12));
                        ++currQuadBytes;
                        // need room for middle byte?
                        if (currQuadBytes >= 4) {
                            if (qlen >= quads.length) {
                                mQuadBuffer = quads = growArrayBy(quads, quads.length);
                            }
                            quads[qlen++] = currQuad;
                            currQuad = 0;
                            currQuadBytes = 0;
                        }
                        currQuad = (currQuad << 8) | (0x80 | ((ch >> 6) & 0x3f));
                        ++currQuadBytes;
                    }
                    // And same last byte in both cases, gets output below:
                    ch = 0x80 | (ch & 0x3f);
                }
            }
            // Ok, we have one more byte to add at any rate:
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = (currQuad << 8) | ch;
            } else {
                if (qlen >= quads.length) {
                    mQuadBuffer = quads = growArrayBy(quads, quads.length);
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (mInputPtr >= mInputLast) {
                if (!loadMore()) {
                    reportInvalidEOF(" in field name");
                }
            }
            ch = mInputBuffer[mInputPtr++] & 0xFF;
        }

        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                mQuadBuffer = quads = growArrayBy(quads, quads.length);
            }
            quads[qlen++] = currQuad;
        }

        Name name = mSymbols.findName(quads, qlen);
        if (name == null) {
            name = addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    private final Name findName(int q1, int lastQuadBytes)
        throws JsonParseException
    {
        // Usually we'll find it from the canonical symbol table already
        Name name = mSymbols.findName(q1);
        if (name != null) {
            return name;
        }
        // If not, more work. We'll need add stuff to buffer
        mQuadBuffer[0] = q1;
        return addName(mQuadBuffer, 1, lastQuadBytes);
    }

    private final Name findName(int q1, int q2, int lastQuadBytes)
        throws JsonParseException
    {
        // Usually we'll find it from the canonical symbol table already
        Name name = mSymbols.findName(q1, q2);
        if (name != null) {
            return name;
        }
        // If not, more work. We'll need add stuff to buffer
        mQuadBuffer[0] = q1;
        mQuadBuffer[1] = q2;
        return addName(mQuadBuffer, 2, lastQuadBytes);
    }

    private final Name findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes)
        throws JsonParseException
    {
        if (qlen >= quads.length) {
            mQuadBuffer = quads = growArrayBy(quads, quads.length);
        }
        quads[qlen++] = lastQuad;
        Name name = mSymbols.findName(quads, qlen);
        if (name == null) {
            return addName(quads, qlen, lastQuadBytes);
        }
        return name;
    }

    /**
     * This is the main workhorse method used when we take a symbol
     * table miss. It needs to demultiplex individual bytes, decode
     * multi-byte chars (if any), and then construct Name instance
     * and add it to the symbol table.
     */
    private final Name addName(int[] quads, int qlen, int lastQuadBytes)
        throws JsonParseException
    {
        /* Ok: must decode UTF-8 chars. No other validation is
         * needed, since unescaping has been done earlier as necessary
         * (as well as error reporting for unescaped control chars)
         */
        // 4 bytes per quad, except last one maybe less
        int byteLen = (qlen << 2) - 4 + lastQuadBytes;

        /* And last one is not correctly aligned (leading zero bytes instead
         * need to shift a bit, instead of trailing). Only need to shift it
         * for UTF-8 decoding; need revert for storage (since key will not
         * be aligned, to optimize lookup speed)
         */
        int lastQuad;

        if (lastQuadBytes < 4) {
            lastQuad = quads[qlen-1];
            // 8/16/24 bit left shift
            quads[qlen-1] = (lastQuad << ((4 - lastQuadBytes) << 3));
        } else {
            lastQuad = 0;
        }

        // Need some working space, TextBuffer works well:
        char[] cbuf = mTextBuffer.emptyAndGetCurrentSegment();
        int cix = 0;

        for (int ix = 0; ix < byteLen; ) {
            int ch = quads[ix >> 2]; // current quad, need to shift+mask
            int byteIx = (ix & 3);
            ch = (ch >> ((3 - byteIx) << 3)) & 0xFF;
            ++ix;

            if (ch > 127) { // multi-byte
                int needed;
                if ((ch & 0xE0) == 0xC0) { // 2 bytes (0x0080 - 0x07FF)
                    ch &= 0x1F;
                    needed = 1;
                } else if ((ch & 0xF0) == 0xE0) { // 3 bytes (0x0800 - 0xFFFF)
                    ch &= 0x0F;
                    needed = 2;
                } else if ((ch & 0xF8) == 0xF0) { // 4 bytes; double-char with surrogates and all...
                    ch &= 0x07;
                    needed = 3;
                } else { // 5- and 6-byte chars not valid xml chars
                    reportInvalidInitial(ch);
                    needed = ch = 1; // never really gets this far
                }
                if ((ix + needed) > byteLen) {
                    reportInvalidEOF(" in field name");
                }
                
                // Ok, always need at least one more:
                int ch2 = quads[ix >> 2]; // current quad, need to shift+mask
                byteIx = (ix & 3);
                ch2 = (ch2 >> ((3 - byteIx) << 3));
                ++ix;
                
                if ((ch2 & 0xC0) != 0x080) {
                    reportInvalidOther(ch2);
                }
                ch = (ch << 6) | (ch2 & 0x3F);
                if (needed > 1) {
                    ch2 = quads[ix >> 2];
                    byteIx = (ix & 3);
                    ch2 = (ch2 >> ((3 - byteIx) << 3));
                    ++ix;
                    
                    if ((ch2 & 0xC0) != 0x080) {
                        reportInvalidOther(ch2);
                    }
                    ch = (ch << 6) | (ch2 & 0x3F);
                    if (needed > 2) { // 4 bytes? (need surrogates on output)
                        ch2 = quads[ix >> 2];
                        byteIx = (ix & 3);
                        ch2 = (ch2 >> ((3 - byteIx) << 3));
                        ++ix;
                        if ((ch2 & 0xC0) != 0x080) {
                            reportInvalidOther(ch2 & 0xFF);
                        }
                        ch = (ch << 6) | (ch2 & 0x3F);
                    }
                }
                if (needed > 2) { // surrogate pair? once again, let's output one here, one later on
                    ch -= 0x10000; // to normalize it starting with 0x0
                    if (cix >= cbuf.length) {
                        cbuf = mTextBuffer.expandCurrentSegment();
                    }
                    cbuf[cix++] = (char) (0xD800 + (ch >> 10));
                    ch = 0xDC00 | (ch & 0x03FF);
                }
            }
            if (cix >= cbuf.length) {
                cbuf = mTextBuffer.expandCurrentSegment();
            }
            cbuf[cix++] = (char) ch;
        }

        /* Ok. Now we have the character array, and can construct the
         * String (as well as check proper composition of semicolons
         * for ns-aware mode...)
         */
        String baseName = new String(cbuf, 0, cix);
        // And finally, unalign if necessary
        if (lastQuadBytes < 4) {
            quads[qlen-1] = lastQuad;
        }
        return mSymbols.addName(baseName, quads, qlen);

    }

    protected void finishString()
        throws IOException, JsonParseException
    {
        int outPtr = 0;
        int c;
        char[] outBuf = mTextBuffer.emptyAndGetCurrentSegment();

        final int[] codes = CharTypes.getInputCodeUtf8();
        final byte[] inputBuffer = mInputBuffer;

        main_loop:
        while (true) {
            // Then the tight ascii non-funny-char loop:
            ascii_loop:
            while (true) {
                int ptr = mInputPtr;
                if (ptr >= mInputLast) {
                    loadMoreGuaranteed();
                    ptr = mInputPtr;
                }
                if (outPtr >= outBuf.length) {
                    outBuf = mTextBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int max = mInputLast;
                {
                    int max2 = ptr + (outBuf.length - outPtr);
                    if (max2 < max) {
                        max = max2;
                    }
                }
                while (ptr < max) {
                    c = (int) inputBuffer[ptr++] & 0xFF;
                    if (codes[c] != 0) {
                        mInputPtr = ptr;
                        break ascii_loop;
                    }
                    outBuf[outPtr++] = (char) c;
                }
                mInputPtr = ptr;
            }
            // Ok: end marker, escape or multi-byte?
            if (c == INT_QUOTE) {
                break main_loop;
            }

            switch (codes[c]) {
            case 1: // backslash
                c = decodeEscaped();
                break;
            case 2: // 2-byte UTF
                c = decodeUtf8_2(c);
                break;
            case 3: // 3-byte UTF
                if ((mInputLast - mInputPtr) >= 2) {
                    c = decodeUtf8_3fast(c);
                } else {
                    c = decodeUtf8_3(c);
                }
                break;
            case 4: // 4-byte UTF
                c = decodeUtf8_4(c);
                // Let's add first part right away:
                outBuf[outPtr++] = (char) (0xD800 | (c >> 10));
                if (outPtr >= outBuf.length) {
                    outBuf = mTextBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                c = 0xDC00 | (c & 0x3FF);
                // And let the other char output down below
                break;
            default:
                if (c < INT_SPACE) {
                    throwUnquotedSpace(c, "string value");
                }
                // Is this good enough error message?
                reportUnexpectedChar(c, null);
            }
            // Need more room?
            if (outPtr >= outBuf.length) {
                outBuf = mTextBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            // Ok, let's add char to output:
            outBuf[outPtr++] = (char) c;
        }
        mTextBuffer.setCurrentLength(outPtr);
    }

    /**
     * Method called to skim through rest of unparsed String value,
     * if it is not needed. This can be done bit faster if contents
     * need not be stored for future access.
     */
    protected void skipString()
        throws IOException, JsonParseException
    {
        final int[] codes = CharTypes.getInputCodeUtf8();
        final byte[] inputBuffer = mInputBuffer;

        main_loop:
        while (true) {
            int c;

            ascii_loop:
            while (true) {
                int ptr = mInputPtr;
                int max = mInputLast;
                if (ptr >= max) {
                    loadMoreGuaranteed();
                    ptr = mInputPtr;
                    max = mInputLast;
                }
                while (ptr < max) {
                    c = (int) inputBuffer[ptr++] & 0xFF;
                    if (codes[c] != 0) {
                        mInputPtr = ptr;
                        break ascii_loop;
                    }
                }
                mInputPtr = ptr;
            }
            // Ok: end marker, escape or multi-byte?
            if (c == INT_QUOTE) {
                break main_loop;
            }

            switch (codes[c]) {
            case 1: // backslash
                decodeEscaped();
                break;
            case 2: // 2-byte UTF
                skipUtf8_2(c);
                break;
            case 3: // 3-byte UTF
                skipUtf8_3(c);
                break;
            case 4: // 4-byte UTF
                skipUtf8_4(c);
                break;
            default:
                if (c < INT_SPACE) {
                    throwUnquotedSpace(c, "string value");
                }
                // Is this good enough error message?
                reportUnexpectedChar(c, null);
            }
        }
    }

    protected JsonToken matchToken(JsonToken token)
        throws IOException, JsonParseException
    {
        // First char is already matched, need to check the rest
        byte[] matchBytes = token.asByteArray();
        int i = 1;

        for (int len = matchBytes.length; i < len; ++i) {
            if (mInputPtr >= mInputLast) {
                loadMoreGuaranteed();
            }
            if (matchBytes[i] != mInputBuffer[mInputPtr]) {
                reportInvalidToken(token.asString().substring(0, i));
            }
            ++mInputPtr;
        }
        /* Ok, fine; let's not bother checking anything beyond keyword.
         * If there's something wrong there, it'll cause a parsing
         * error later on.
         */
        return (mCurrToken = token);
    }

    private void reportInvalidToken(String matchedPart)
        throws IOException, JsonParseException
    {
        StringBuilder sb = new StringBuilder(matchedPart);
        /* Let's just try to find what appears to be the token, using
         * regular Java identifier character rules. It's just a heuristic,
         * nothing fancy here (nor fast).
         */
        while (true) {
            if (mInputPtr >= mInputLast && !loadMore()) {
                break;
            }
            int i = (int) mInputBuffer[mInputPtr++];
            char c = (char) decodeCharForError(i);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            ++mInputPtr;
            sb.append(c);
        }

        reportError("Unrecognized token '"+sb.toString()+"': was expecting 'null', 'true' or 'false'");
    }

    /*
    ////////////////////////////////////////////////////
    // Internal methods, escape/unescape
    ////////////////////////////////////////////////////
     */

    protected final char decodeEscaped()
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            if (!loadMore()) {
                reportInvalidEOF(" in character escape sequence");
            }
        }
        int c = (int) mInputBuffer[mInputPtr++];

        switch ((int) c) {
            // First, ones that are mapped
        case INT_b:
            return '\b';
        case INT_t:
            return '\t';
        case INT_n:
            return '\n';
        case INT_f:
            return '\f';
        case INT_r:
            return '\r';

            // And these are to be returned as they are
        case INT_QUOTE:
        case INT_SLASH:
        case INT_BACKSLASH:
            return (char) c;

        case INT_u: // and finally hex-escaped
            break;

        default:
            reportError("Unrecognized character escape \\ followed by "+getCharDesc(decodeCharForError(c)));
        }

        // Ok, a hex escape. Need 4 characters
        int value = 0;
        for (int i = 0; i < 4; ++i) {
            if (mInputPtr >= mInputLast) {
                if (!loadMore()) {
                    reportInvalidEOF(" in character escape sequence");
                }
            }
            int ch = (int) mInputBuffer[mInputPtr++];
            int digit = CharTypes.charToHex(ch);
            if (digit < 0) {
                reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
            }
            value = (value << 4) | digit;
        }
        return (char) value;
    }

    protected int decodeCharForError(int firstByte)
        throws IOException, JsonParseException
    {
        int c = (int) firstByte;
        if (c < 0) { // if >= 0, is ascii and fine as is
            int needed;
            
            // Ok; if we end here, we got multi-byte combination
            if ((c & 0xE0) == 0xC0) { // 2 bytes (0x0080 - 0x07FF)
                c &= 0x1F;
                needed = 1;
            } else if ((c & 0xF0) == 0xE0) { // 3 bytes (0x0800 - 0xFFFF)
                c &= 0x0F;
                needed = 2;
            } else if ((c & 0xF8) == 0xF0) {
                // 4 bytes; double-char with surrogates and all...
                c &= 0x07;
                needed = 3;
            } else {
                reportInvalidInitial(c & 0xFF);
                needed = 1; // never gets here
            }

            int d = nextByte();
            if ((d & 0xC0) != 0x080) {
                reportInvalidOther(d & 0xFF);
            }
            c = (c << 6) | (d & 0x3F);
            
            if (needed > 1) { // needed == 1 means 2 bytes total
                d = nextByte(); // 3rd byte
                if ((d & 0xC0) != 0x080) {
                    reportInvalidOther(d & 0xFF);
                }
                c = (c << 6) | (d & 0x3F);
                if (needed > 2) { // 4 bytes? (need surrogates)
                    d = nextByte();
                    if ((d & 0xC0) != 0x080) {
                        reportInvalidOther(d & 0xFF);
                    }
                    c = (c << 6) | (d & 0x3F);
                }
            }
        }
        return c;
    }

    /*
    ////////////////////////////////////////////////////
    // Internal methods,UTF8 decoding
    ////////////////////////////////////////////////////
     */

    private final int decodeUtf8_2(int c)
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        int d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        return ((c & 0x1F) << 6) | (d & 0x3F);
    }

    private final int decodeUtf8_3(int c1)
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        c1 &= 0x0F;
        int d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        int c = (c1 << 6) | (d & 0x3F);
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        c = (c << 6) | (d & 0x3F);
        return c;
    }

    private final int decodeUtf8_3fast(int c1)
        throws IOException, JsonParseException
    {
        c1 &= 0x0F;
        int d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        int c = (c1 << 6) | (d & 0x3F);
        d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        c = (c << 6) | (d & 0x3F);
        return c;
    }

    /**
     * @return Character value <b>minus 0x10000</c>; this so that caller
     *    can readily expand it to actual surrogates
     */
    private final int decodeUtf8_4(int c)
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        int d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        c = ((c & 0x07) << 6) | (d & 0x3F);

        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        c = (c << 6) | (d & 0x3F);
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }

        /* note: won't change it to negative here, since caller
         * already knows it'll need a surrogate
         */
        return ((c << 6) | (d & 0x3F)) - 0x10000;
    }

    private final void skipUtf8_2(int c)
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        c = (int) mInputBuffer[mInputPtr++];
        if ((c & 0xC0) != 0x080) {
            reportInvalidOther(c & 0xFF, mInputPtr);
        }
    }

    /* Alas, can't heavily optimize skipping, since we still have to
     * do validity checks...
     */
    private final void skipUtf8_3(int c)
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        //c &= 0x0F;
        c = (int) mInputBuffer[mInputPtr++];
        if ((c & 0xC0) != 0x080) {
            reportInvalidOther(c & 0xFF, mInputPtr);
        }
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        c = (int) mInputBuffer[mInputPtr++];
        if ((c & 0xC0) != 0x080) {
            reportInvalidOther(c & 0xFF, mInputPtr);
        }
    }

    private final void skipUtf8_4(int c)
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        int d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        d = (int) mInputBuffer[mInputPtr++];
        if ((d & 0xC0) != 0x080) {
            reportInvalidOther(d & 0xFF, mInputPtr);
        }
    }

    /*
    ////////////////////////////////////////////////////
    // Internal methods, input loading
    ////////////////////////////////////////////////////
     */

    private int nextByte()
        throws IOException, JsonParseException
    {
        if (mInputPtr >= mInputLast) {
            loadMoreGuaranteed();
        }
        return mInputBuffer[mInputPtr++] & 0xFF;
    }

    /*
    ////////////////////////////////////////////////////
    // Internal methods, error reporting
    ////////////////////////////////////////////////////
     */

    protected void reportInvalidInitial(int mask)
        throws JsonParseException
    {
        reportError("Invalid UTF-8 start byte 0x"+Integer.toHexString(mask));
    }

    protected void reportInvalidOther(int mask)
        throws JsonParseException
    {
        reportError("Invalid UTF-8 middle byte 0x"+Integer.toHexString(mask));
    }

    protected void reportInvalidOther(int mask, int ptr)
        throws JsonParseException
    {
        mInputPtr = ptr;
        reportInvalidOther(mask);
    }

    public static int[] growArrayBy(int[] arr, int more)
    {
        if (arr == null) {
            return new int[more];
        }
        int[] old = arr;
        int len = arr.length;
        arr = new int[len + more];
        System.arraycopy(old, 0, arr, 0, len);
        return arr;
    }

}