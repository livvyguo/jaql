/* Generated By:JavaCC: Do not edit this line. JsonParser.java */
package com.ibm.jaql.json.parser;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;

import com.ibm.jaql.json.constructor.JsonConstructor;
import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.json.type.JsonString;
import com.ibm.jaql.json.type.JsonLong;
import com.ibm.jaql.json.type.JsonDecimal;
import com.ibm.jaql.json.type.JsonDouble;
import com.ibm.jaql.json.type.JsonBool;
import com.ibm.jaql.json.type.BufferedJsonArray;
import com.ibm.jaql.json.type.BufferedJsonRecord;
import com.ibm.jaql.json.type.JsonEncoding;

import com.ibm.jaql.util.BaseUtil;

/** 
 *  This class is generated by JavaCC. Don't modify it directly.
 */
public class JsonParser implements JsonParserConstants {
  public static final JsonValue NIL = new JsonValue() {
    @Override
    public int compareTo(Object obj)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public JsonEncoding getEncoding()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public long longHashCode()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setCopy(JsonValue jvalue) throws Exception
    {
      throw new UnsupportedOperationException();
    }
  };

  public JsonParser()
  {
        this(new StringReader(""));
  }

  public JsonValue parse(String jsonString) throws ParseException
  {
    ReInit(new StringReader(jsonString));
    try
    {
      return TopVal();
    }
    catch (ParseException e)
    {
      // rethrow to include the original jsonString:
      throw new ParseException("Cannot parse '" +jsonString+ "': " + e.getMessage());
    }
  }

//  syntax BNF:
//  TopVal    ::= JsonVal
//  JsonVal    ::= MapVal | ArrayVal | AtomVal
//  MapVal    ::= { MapFields }
//  MapFields ::= Field_name : TopVal (, MapFields)*
//  ArrayVal  ::= [ ArrayItems ]
//  ArrayItems::= TopVal (, ArrayItems)*
//  AtomVal   ::= <strvalue> | <long> | <decimal>
//
// This parser can be used in the following ways:
//
// 1) To parse a single JSON value, which is placed all in memory:
//
//        JsonValue value = parser.JsonVal();
//        parser.Eof(); // optional EOF check
//
// 2) To parse a single JSON value and do the EOF check in one call (same as above):
//
//       JsonValue value = parser.TopVal();
// 
// 3) To parse a top-level array of JSON values, one at a time
//
//        for( JsonValue value = parser.ArrayFirst() ; value != JsonParser.NIL ; value = parser.ArrayNext() )  
//        {
//           // process value
//        }
//        parser.Eof(); // optional EOF check
//
//    Note: The parser keeps only one value in memory at a time, but a new value is returned
//    each time so it is safe to keep a handle on prior values.
//
// If you want to ensure that the entire buffer was processed, parser.Eof() can be called
// after JsonVal() or when ArrayFirst()/ArrayNext() returns null.  
  final public JsonValue JsonVal() throws ParseException {
        JsonValue value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ARRAY_BEGIN:
      value = ArrayVal();
      break;
    case MAP_BEGIN:
      value = MapVal();
      break;
    case LONG:
    case REAL:
    case DECIMAL:
    case DOUBLE:
    case TRUE:
    case FALSE:
    case NULL:
    case ID:
    case STRING1:
    case STRING2:
      value = AtomVal();
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public void Eof() throws ParseException {
    jj_consume_token(0);
  }

  final public JsonValue TopVal() throws ParseException {
        JsonValue value;
    value = JsonVal();
    Eof();
          {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public JsonValue ArrayFirst() throws ParseException {
        JsonValue value = JsonParser.NIL;
    jj_consume_token(ARRAY_BEGIN);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MAP_BEGIN:
    case ARRAY_BEGIN:
    case LONG:
    case REAL:
    case DECIMAL:
    case DOUBLE:
    case TRUE:
    case FALSE:
    case NULL:
    case ID:
    case STRING1:
    case STRING2:
      value = JsonVal();
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
          {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public JsonValue ArrayNext() throws ParseException {
        JsonValue value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA:
      jj_consume_token(COMMA);
      value = JsonVal();
                                          {if (true) return value;}
      break;
    case ARRAY_END:
      jj_consume_token(ARRAY_END);
                                                  {if (true) return JsonParser.NIL;}
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public JsonValue ArrayVal() throws ParseException {
        JsonValue[] values;
    jj_consume_token(ARRAY_BEGIN);
    values = ValueList();
    jj_consume_token(ARRAY_END);
          {if (true) return new BufferedJsonArray(values);}
    throw new Error("Missing return statement in function");
  }

  final public JsonValue MapVal() throws ParseException {
        BufferedJsonRecord jRecord = new BufferedJsonRecord();
    jj_consume_token(MAP_BEGIN);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ID:
    case STRING1:
    case STRING2:
      MapField(jRecord);
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_1;
        }
        jj_consume_token(COMMA);
        MapField(jRecord);
      }
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    jj_consume_token(MAP_END);
          {if (true) return jRecord;}
    throw new Error("Missing return statement in function");
  }

  final public void MapField(BufferedJsonRecord jRecord) throws ParseException {
        String name;
        JsonValue value;
    name = Name();
    jj_consume_token(COLON);
    value = JsonVal();
          jRecord.add(name, value);
  }

  final public String Name() throws ParseException {
        Token tok;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ID:
      tok = jj_consume_token(ID);
                        {if (true) return tok.image;}
      break;
    case STRING1:
      tok = jj_consume_token(STRING1);
                        {if (true) return tok.image;}
      break;
    case STRING2:
      tok = jj_consume_token(STRING2);
                        {if (true) return tok.image;}
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public JsonValue AtomVal() throws ParseException {
        JsonValue value;
        Token atom;
        String s;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING1:
    case STRING2:
      s = String();
                             value = new JsonString(s);
      break;
    case LONG:
      atom = jj_consume_token(LONG);
                             value = new JsonLong(atom.image);
      break;
    case REAL:
      atom = jj_consume_token(REAL);
                             value = new JsonDecimal(atom.image);
      break;
    case DECIMAL:
      atom = jj_consume_token(DECIMAL);
                             value = new JsonDecimal(atom.image);
      break;
    case DOUBLE:
      atom = jj_consume_token(DOUBLE);
                             value = new JsonDouble(atom.image);
      break;
    case TRUE:
      atom = jj_consume_token(TRUE);
                             value = JsonBool.TRUE;
      break;
    case FALSE:
      atom = jj_consume_token(FALSE);
                             value = JsonBool.FALSE;
      break;
    case NULL:
      atom = jj_consume_token(NULL);
                             value = null;
      break;
    case ID:
      value = Construct();
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public JsonValue Construct() throws ParseException {
        Token fn;
        JsonValue[] values;
    fn = jj_consume_token(ID);
    jj_consume_token(LPAREN);
    values = ValueList();
    jj_consume_token(RPAREN);
          {if (true) return JsonConstructor.eval(fn.image, values);}
    throw new Error("Missing return statement in function");
  }

  final public JsonValue[] ValueList() throws ParseException {
    ArrayList<JsonValue> values = new ArrayList<JsonValue>();
    JsonValue value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MAP_BEGIN:
    case ARRAY_BEGIN:
    case LONG:
    case REAL:
    case DECIMAL:
    case DOUBLE:
    case TRUE:
    case FALSE:
    case NULL:
    case ID:
    case STRING1:
    case STRING2:
      value = JsonVal();
                                  values.add(value);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        value = JsonVal();
                                  values.add(value);
      }
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
      {if (true) return values.toArray(new JsonValue[values.size()]);}
    throw new Error("Missing return statement in function");
  }

  final public String String() throws ParseException {
        String str;
        String str2;
    str = String1();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
      case STRING1:
      case STRING2:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
        jj_consume_token(PLUS);
        break;
      default:
        jj_la1[10] = jj_gen;
        ;
      }
      str2 = String1();
                                                   str += str2;
    }
          {if (true) return str;}
    throw new Error("Missing return statement in function");
  }

  final public String String1() throws ParseException {
        Token tok;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING1:
      tok = jj_consume_token(STRING1);
                        {if (true) return tok.image;}
      break;
    case STRING2:
      tok = jj_consume_token(STRING2);
                        {if (true) return tok.image;}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public JsonParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[12];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xcff0a00,0xcff0a00,0x1040,0x40,0xc800000,0xc800000,0xcff0000,0x40,0xcff0a00,0xc008000,0x8000,0xc000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
   }

  /** Constructor with InputStream. */
  public JsonParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public JsonParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new JsonParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public JsonParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new JsonParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public JsonParser(JsonParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(JsonParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[49];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 12; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 49; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
