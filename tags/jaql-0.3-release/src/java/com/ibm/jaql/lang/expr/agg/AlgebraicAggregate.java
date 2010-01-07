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
package com.ibm.jaql.lang.expr.agg;

import com.ibm.jaql.lang.core.Env;
import com.ibm.jaql.lang.core.Var;
import com.ibm.jaql.lang.expr.core.ArrayExpr;
import com.ibm.jaql.lang.expr.core.CombineExpr;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.ForExpr;
import com.ibm.jaql.lang.expr.core.MacroExpr;

/**
 * 
 */
public abstract class AlgebraicAggregate extends MacroExpr
{
  /**
   * @param exprs
   */
  public AlgebraicAggregate(Expr[] exprs)
  {
    super(exprs);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.jaql.lang.expr.core.MacroExpr#expand(com.ibm.jaql.lang.core.Env)
   */
  @Override
  public Expr expand(Env env) throws Exception
  {
    if (exprs.length != 1)
    {
      throw new RuntimeException("Algebraic functions must have one argument!");
    }
    Var vi = env.makeVar("$i");
    Var va = env.makeVar("$a");
    Var vb = env.makeVar("$b");
    Expr initExpr = initExpr(vi);
    initExpr = new ArrayExpr(initExpr);
    ForExpr initLoop = new ForExpr(vi, exprs[0], initExpr);
    // DistributiveAggregate dagg = aggExpr(initLoop);
    Expr combineExpr = combineExpr(va, vb);
    // Expr emptyExpr = emptyExpr();
    // combineExpr = new CombineExpr(va, vb, initLoop, combineExpr, emptyExpr); 
    combineExpr = new CombineExpr(va, vb, initLoop, combineExpr);
    Expr finalExpr = finalExpr(combineExpr);
    return finalExpr;
  }

  /**
   * @param var
   * @return
   * @throws Exception
   */
  abstract protected Expr initExpr(Var var) throws Exception;
  // abstract protected DistributiveAggregate aggExpr(Expr initLoop) throws Exception;
  /**
   * @param var1
   * @param var2
   * @return
   * @throws Exception
   */
  abstract protected Expr combineExpr(Var var1, Var var2) throws Exception;
  /**
   * @param agg
   * @return
   * @throws Exception
   */
  abstract protected Expr finalExpr(Expr agg) throws Exception;

  //  protected Expr emptyExpr() throws Exception
  //  {
  //    return new ConstExpr(Item.nil);
  //  }
}