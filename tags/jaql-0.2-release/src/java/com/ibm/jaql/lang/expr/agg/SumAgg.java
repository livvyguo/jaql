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

import java.math.BigDecimal;
import java.math.MathContext;

import com.ibm.jaql.json.type.Item;
import com.ibm.jaql.json.type.JDecimal;
import com.ibm.jaql.json.type.JDouble;
import com.ibm.jaql.json.type.JLong;
import com.ibm.jaql.json.type.JValue;
import com.ibm.jaql.lang.core.Context;
import com.ibm.jaql.lang.expr.core.Expr;
import com.ibm.jaql.lang.expr.core.JaqlFn;


/**
 * 
 */
@JaqlFn(fnName = "sum", minArgs = 1, maxArgs = 1)
public final class SumAgg extends AlgebraicAggregate
{
  public static final class Summer
  {
    private boolean sawLong;
    private boolean sawDouble;
    private long lsum;
    private double dblSum;
    private BigDecimal decSum;
    private Item result = new Item();

    public void init()
    {
      sawDouble = sawLong = false;
      dblSum = lsum = 0;
      decSum = null;
    }
    
    public void add(Item item)
    {
      JValue w = item.get();
      if( w instanceof JLong )
      {
        if( sawDouble )
        {
          throw new RuntimeException("cannot sum doubles and decimals");
        }
        sawLong = true;
        lsum += ((JLong)w).value;
      }
      else if( w instanceof JDouble )
      {
        if( sawLong || decSum != null )
        {
          throw new RuntimeException("cannot sum doubles and decimals");
        }
        sawDouble = true;
        dblSum += ((JDouble)w).value;
      }
      else
      {
        JDecimal n = (JDecimal)w;      // TODO: need a mutable BigDecimal...
        if( decSum == null )
        {
          decSum = n.value;
        }
        else
        {
          decSum = decSum.add(n.value);
        }
      }
    }
    
    public Item get()
    {
      JValue v;
      if( sawDouble )
      {
        v = new JDouble(dblSum); // TODO: memory
      }
      else if( decSum == null )
      {
        if( sawLong )
        {
          v = new JLong(lsum);  // TODO: memory
        }
        else
        {
          v = null;
        }
      }
      else
      {
        if( lsum != 0 )
        {
          decSum = decSum.add(new BigDecimal(lsum), MathContext.DECIMAL128);
        }
        v = new JDecimal(decSum); // TODO: memory
      }
      result.set(v);
      return result;
    }
  }
  
  Summer summer = new Summer();
  
  /**
   * @param exprs
   */
  public SumAgg(Expr[] exprs)
  {
    super(exprs);
  }

  /**
   * @param expr
   */
  public SumAgg(Expr expr)
  {
    super(expr);
  }

  @Override
  public void initInitial(Context context) throws Exception
  {
    summer.init();
  }

  @Override
  public void addInitial(Item item) throws Exception
  {
    summer.add(item);
  }

  @Override
  public Item getPartial() throws Exception
  {
    return summer.get();
  }

  @Override
  public void addPartial(Item item) throws Exception
  {
    summer.add(item);
  }

  @Override
  public Item getFinal() throws Exception
  {
    return summer.get();
  }
}


///**
// * 
// */
//@JaqlFn(fnName = "sum", minArgs = 1, maxArgs = 1)
//public class SumAgg extends AlgebraicAggregate // DistributiveAggregate
//{
//  /**
//   * @param exprs
//   */
//  public SumAgg(Expr[] exprs)
//  {
//    super(exprs);
//  }
//
//  /**
//   * @param expr
//   */
//  public SumAgg(Expr expr)
//  {
//    super(new Expr[]{expr});
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see com.ibm.jaql.lang.expr.agg.AlgebraicAggregate#initExpr(com.ibm.jaql.lang.core.Var)
//   */
//  @Override
//  protected Expr initExpr(Var var) throws Exception
//  {
//    return new VarExpr(var);
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see com.ibm.jaql.lang.expr.agg.AlgebraicAggregate#combineExpr(com.ibm.jaql.lang.core.Var,
//   *      com.ibm.jaql.lang.core.Var)
//   */
//  @Override
//  protected Expr combineExpr(Var var1, Var var2) throws Exception
//  {
//    return new MathExpr(MathExpr.PLUS, new VarExpr(var1), new VarExpr(var2));
//  }
//
//  /*
//   * (non-Javadoc)
//   * 
//   * @see com.ibm.jaql.lang.expr.agg.AlgebraicAggregate#finalExpr(com.ibm.jaql.lang.expr.core.Expr)
//   */
//  @Override
//  protected Expr finalExpr(Expr agg) throws Exception
//  {
//    return agg;
//  }
//
//  //  public Item eval(final Context context) throws Exception
//  //  {
//  //    boolean sawLong = false;
//  //    long lsum = 0;
//  //    BigDecimal sum = null;
//  //    Iter iter = exprs[0].iter(context);
//  //    if( iter.isNull() )
//  //    {
//  //      return Item.nil;
//  //    }
//  //    Item item;
//  //    while( (item = iter.next()) != null )
//  //    {
//  //      JaqlType w = item.get();
//  //      if( w == null) 
//  //      {
//  //        continue;
//  //      }
//  //      else if( w instanceof LongItem )
//  //      {
//  //        sawLong = true;
//  //        lsum += ((LongItem)w).value;
//  //      }
//  //      else
//  //      {
//  //        DecimalItem n = (DecimalItem)w;
//  //        // TODO: need a mutable BigDecimal...
//  //        if( sum == null )
//  //        {
//  //          sum = n.value;
//  //        }
//  //        else
//  //        {
//  //          sum = sum.add(n.value);
//  //        }
//  //      }
//  //    }
//  //    if( sum == null )
//  //    {
//  //      if( sawLong )
//  //      {
//  //        return new Item(new LongItem(lsum));  // TODO: memory
//  //      }
//  //      return Item.nil;
//  //    }
//  //    else
//  //    {
//  //      if( lsum != 0 )
//  //      {
//  //        sum = sum.add(new BigDecimal(lsum));
//  //      }
//  //      return new Item(new DecimalItem(sum)); // TODO: memory
//  //    }
//  //  }
//}