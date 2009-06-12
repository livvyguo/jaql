/*
 * Copyright (C) IBM Corp. 2008.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.jaql.lang.expr.record;

import com.ibm.jaql.json.type.Item;
import com.ibm.jaql.json.type.JRecord;
import com.ibm.jaql.json.type.JString;
import com.ibm.jaql.json.type.MemoryJRecord;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.JaqlFn;

/**
 * 
 */
@JaqlFn(fnName = "remap", minArgs = 2, maxArgs = 2)
public class RemapFn extends Expr
{
  /**
   * removeFields(oldRec, newRec)
   * 
   * @param exprs
   */
  public RemapFn(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param oldRec
   * @param newRec
   */
  public RemapFn(Expr oldRec, Expr newRec)
  {
    super(new Expr[]{oldRec, newRec});
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.Expr#eval(com.ibm.jaql.lang.core.Context)
   */
  public Item eval(Context context) throws Exception
  {
    Item oldItem = exprs[0].eval(context);
    Item newItem = exprs[1].eval(context);
    JRecord oldRec = (JRecord) oldItem.get();
    JRecord newRec = (JRecord) newItem.get();
    if (oldRec == null)
    {
      return newItem;
    }
    if (newRec == null)
    {
      return oldItem;
    }

    MemoryJRecord outRec = new MemoryJRecord(); // TODO: memory
    Item result = new Item(outRec); // TODO: memory

    int n = newRec.arity();
    for (int i = 0; i < n; i++)
    {
      outRec.add(newRec.getName(i), newRec.getValue(i));
    }

    n = oldRec.arity();
    for (int i = 0; i < n; i++)
    {
      JString nm = oldRec.getName(i);
      if (newRec.getValue(nm, null) == null)
      {
        outRec.add(nm, oldRec.getValue(i));
      }
    }

    return result;
  }
}
