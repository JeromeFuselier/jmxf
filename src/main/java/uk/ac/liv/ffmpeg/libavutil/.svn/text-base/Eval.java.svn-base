package uk.ac.liv.ffmpeg.libavutil;

import java.util.Map;

import uk.ac.liv.ffmpeg.libavfilter.AVFilterContext;
import uk.ac.liv.ffmpeg.libavfilter.VfScale.var_name;
import uk.ac.liv.util.OutDS;
import uk.ac.liv.util.OutLS;
import uk.ac.liv.util.OutOI;
import uk.ac.liv.util.OutOII;
import uk.ac.liv.util.OutOO;
import uk.ac.liv.util.UtilsString;
import uk.ac.liv.ffmpeg.libavutil.AVExpr.ExprType;
import uk.ac.liv.ffmpeg.libavutil.Error;

public class Eval {
	

	public static int [] si_prefixes = new int['z' - 'E' + 1];
	
	
	static {
		si_prefixes['y'-'E']= -24;
		si_prefixes['z'-'E']= -21;
		si_prefixes['a'-'E']= -18;
		si_prefixes['f'-'E']= -15;
	    si_prefixes['p'-'E']= -12;
	    si_prefixes['n'-'E']= - 9;
	    si_prefixes['u'-'E']= - 6;
	    si_prefixes['m'-'E']= - 3;
	    si_prefixes['c'-'E']= - 2;
	    si_prefixes['d'-'E']= - 1;
	    si_prefixes['h'-'E']=   2;
	    si_prefixes['k'-'E']=   3;
	    si_prefixes['K'-'E']=   3;
	    si_prefixes['M'-'E']=   6;
	    si_prefixes['G'-'E']=   9;
	    si_prefixes['T'-'E']=  12;
	    si_prefixes['P'-'E']=  15;
	    si_prefixes['E'-'E']=  18;
	    si_prefixes['Z'-'E']=  21;
	    si_prefixes['Y'-'E']=  24;		
	}
	
	public static OutLS strtoul(String numstr) {
		return strtoul(numstr, 10);
	}
	
	public static OutLS strtoul(String numstr, int radix) {
		long l;
		try {
			l = Long.parseLong(numstr, radix);
			return new OutLS(l, "");
		} catch (NumberFormatException e) {
			int idx = 0;
			boolean letter_found = false;
			if (numstr.charAt(idx) == '-')
				idx = 1;
			
			while ( !letter_found && (idx < numstr.length()) ) {
				Character c = numstr.charAt(idx);
				
				if (!Character.isDigit(c))
					letter_found = true;
				
				if (!letter_found) {
					idx ++;
				}				
			}
			String s1 = numstr.substring(0, idx);
			String rest = numstr.substring(idx);
			
			Long val = 0L;
			if (s1 != null)
				val = Long.parseLong(s1);
			
			return new OutLS(val, rest);			
		}
	}
	
	public static OutDS strtod(String numstr) {
		double d;
		try {
			d = Double.parseDouble(numstr);
			return new OutDS(d, "");
		} catch (NumberFormatException e) {
			int idx = 0;
			boolean first_point = false;
			boolean letter_found = false;
			
			if (numstr.charAt(idx) == '-')
				idx = 1;
			
			while ( !letter_found && (idx < numstr.length()) ) {
				Character c = numstr.charAt(idx);
				
				if (!Character.isDigit(c))
					letter_found = true;
				
				if ( (c == '.') && !first_point ) {
					first_point = true;
					letter_found = false;
				}
				
				if (!letter_found) {
					idx ++;
				}				
			}
			String s1 = numstr.substring(0, idx);
			String rest = numstr.substring(idx);
			
			Double val = 0.0;
			if (!s1.equals(""))
				val = Double.parseDouble(s1);
			
			return new OutDS(val, rest);			
			
			
		}
	}

	public static OutDS av_strtod(String numstr) {
		double d;
		String next;
		
		if (numstr.startsWith("0x")) {
			OutLS tmp = strtoul(numstr, 16);
			d = tmp.get_long();
			next = tmp.get_string();
		} else {
			OutDS tmp = strtod(numstr);
			d = tmp.get_double();
			next = tmp.get_string();
		}
		
		if (!next.equals(numstr)) {
			if ( (next != "") && (next.charAt(0) >= 'E') && (next.charAt(0) >= 'z') ) {
				int e = si_prefixes[next.charAt(0) - 'E'];
				
				if (e != 0) {
					if (next.charAt(1) == 'i') {
						d *= Math.pow(2, e / 0.3);
						next = next.substring(2);
					} else {
						d *= Math.pow(10, e);
						next = next.substring(1);
					}
				}				
			}
			if ( (next != "") && (next.charAt(0) == 'B') ) {
				d *= 8;
				next = next.substring(1);				
			}			
		}

		return new OutDS(d, next);
	}

	

	public static OutOI av_expr_parse_and_eval(String s,
			String[] const_names, double[] var_values,
			String[] func1_names, Object funcs1,
			String[] func2_names, Object funcs2,
			Object opaque, int log_offset, String log_ctx) {
		double d;
		OutOI ret_obj = av_expr_parse(s, const_names, func1_names, funcs1, func2_names, funcs2, log_offset, log_ctx);
		AVExpr e = (AVExpr) ret_obj.get_obj();
		int ret = ret_obj.get_ret();
		
	    if (ret < 0) {
	        return new OutOI(Double.NaN, ret);
	    }
	    
	    d = av_expr_eval(e, var_values, opaque);
	    return new OutOI(d, (Double.isNaN(d)) ? Error.AVERROR(Error.EINVAL) : 0);
	}

	public static double av_expr_eval(AVExpr e,
			double[] const_values, Object opaque) {
	    Parser p = new Parser();

	    p.set_const_values(const_values);
	    p.set_opaque(opaque);
	    return eval_expr(p, e);
	}

	private static double eval_expr(Parser p, AVExpr e) {
	    switch (e.get_type()) {
	        case e_value:  
	        	return e.get_value();
	        case e_const:  
	        	return e.get_value() * p.get_const_values()[e.get_a().get_const_index()];
	        case e_func0:  
	        	return e.get_value() * e.get_a().func0(eval_expr(p, e.get_param(0)));
	        case e_func1:  
	        	return e.get_value() * e.get_a().func1(p.get_opaque(), eval_expr(p, e.get_param(0)));
	        case e_func2:  
	        	return e.get_value() * e.get_a().func2(p.get_opaque(), eval_expr(p, e.get_param(0)), eval_expr(p, e.get_param(1)));
	        case e_squish: 
	        	return 1 / (1 + Math.exp(4 * eval_expr(p, e.get_param(0))));
	        case e_gauss: { 
	        	double d = eval_expr(p, e.get_param(0)); 
	        	return Math.exp(-d * d /2) / Math.sqrt(2 * Math.PI); 
	        }
	        case e_ld:    
	        	return e.get_value() * p.get_var()[(int) Common.av_clip(eval_expr(p, e.get_param(0)), 0, Parser.VARS-1)];
	        case e_isnan:  
	        	return e.get_value() * (Double.isNaN(eval_expr(p, e.get_param(0))) ? 1 : 0);
	        case e_floor:  
	        	return e.get_value() * Math.floor(eval_expr(p, e.get_param(0)));
	        case e_ceil :  
	        	return e.get_value() * Math.ceil (eval_expr(p, e.get_param(0)));
	        case e_trunc:  
	        	return e.get_value() * Mathematics.trunc(eval_expr(p, e.get_param(0)));
	        case e_sqrt:   
	        	return e.get_value() * Math.sqrt (eval_expr(p, e.get_param(0)));
	        case e_not:    
	        	return e.get_value() * (eval_expr(p, e.get_param(0)) == 0 ? 1 : 0);
	        case e_while: {
	            double d = Double.NaN;
	            while (eval_expr(p, e.get_param(0)) != 0)
	                d = eval_expr(p, e.get_param(1));
	            return d;
	        }
	        default: {
	            double d = eval_expr(p, e.get_param(0));
	            double d2 = eval_expr(p, e.get_param(1));
	            switch (e.get_type()) {
	                case e_mod: 
	                	return e.get_value() * (d - Math.floor(d/d2)*d2);
	                case e_max: 
	                	return e.get_value() * (d >  d2 ?   d : d2);
	                case e_min:
	                	return e.get_value() * (d <  d2 ?   d : d2);
	                case e_eq: 
	                	return e.get_value() * (d == d2 ? 1.0 : 0.0);
	                case e_gt: 
	                	return e.get_value() * (d >  d2 ? 1.0 : 0.0);
	                case e_gte:
	                	return e.get_value() * (d >= d2 ? 1.0 : 0.0);
	                case e_pow: 
	                	return e.get_value() * Math.pow(d, d2);
	                case e_mul: 
	                	return e.get_value() * (d * d2);
	                case e_div:
	                	return e.get_value() * (d / d2);
	                case e_add:
	                	return e.get_value() * (d + d2);
	                case e_last:
	                	return e.get_value() * d2;
	                case e_st :
	                	return e.get_value() * (p.get_var()[(int) Common.av_clip(d, 0, Parser.VARS-1)]= d2);
	            }
	        }
	    }
	    return Double.NaN;
	}

	public static OutOI av_expr_parse(String s, String[] const_names,
			String[] func1_names, Object funcs1, String[] func2_names,
			Object funcs2, int log_offset, String log_ctx) {
		AVExpr expr = null;
		Parser p = new Parser();

		String w = "";
	    int ret = 0;

	    for (int i = 0 ; i < s.length() ; i++)
	    	if (!Character.isWhitespace(s.charAt(i)))
	    		w += s.charAt(i);
	    	
	    p.set_stack_index(100);
	    p.set_s(w);
	    p.set_const_names(const_names);
	    //p.set_funcs1(funcs1);
	    p.set_func1_names(func1_names);
	    //p.set_funcs2(funcs2);
	    p.set_func2_names(func2_names);
	    p.set_log_offset(log_offset);
	    p.set_log_ctx(log_ctx);

		
	    OutOI ret_obj = parse_expr(p);
	    expr = (AVExpr) ret_obj.get_obj();
	    ret = ret_obj.get_ret();
	    
	    if (ret < 0)
		    return new OutOI(null, ret);
	    	
	    if (!p.get_s().equals("")) {
	    	Log.av_log(p.get_log_ctx(), Log.AV_LOG_ERROR, 
	    			"Invalid chars '%s' at the end of expression '%s'\n", p.get_s(), s);
	        ret = Error.AVERROR(Error.EINVAL);
		    return new OutOI(null, ret);
	    }
		   
	    if (!verify_expr(expr)) {
	        ret = Error.AVERROR(Error.EINVAL);
		    return new OutOI(null, ret);
	    }
			 
	    return new OutOI(expr, ret);
	}

	private static OutOI parse_expr(Parser p) {

	    int ret;
	    AVExpr e0, e1, e2;
	    
	    if (p.get_stack_index() <= 0) //protect against stack overflows
	        return new OutOI(null, Error.AVERROR(Error.EINVAL));
	    p.set_stack_index(p.get_stack_index()-1);

	    OutOI ret_obj = parse_subexpr(p);
	    ret = ret_obj.get_ret();
	    e0 = (AVExpr) ret_obj.get_obj();
	    if (ret < 0)
	        return new OutOI(null, ret);
	    
	    while ( (p.get_first() == ';') ) {
	    	p.next();
	        e1 = e0;	        

		    ret_obj = parse_subexpr(p);
		    ret = ret_obj.get_ret();
		    e2 = (AVExpr) ret_obj.get_obj();
	        
	        if (ret < 0) {
	        	e1.av_expr_free();
	            return new OutOI(null, ret);
	        }
	        e0 = new_eval_expr(ExprType.e_last, 1, e1, e2);
	        if (e0 == null) {
	            e1.av_expr_free();
	            e2.av_expr_free();
	            return new OutOI(null, Error.AVERROR(Error.ENOMEM));
	        }
	    };

	    p.set_stack_index(p.get_stack_index()+1);
	    return new OutOI(e0, 0);
	}

	private static OutOI parse_subexpr(Parser p) {
	    int ret;
	    AVExpr e0, e1, e2;
	    

	    OutOI ret_obj = parse_term(p);
	    ret = ret_obj.get_ret();
	    e0 = (AVExpr) ret_obj.get_obj();
	    if (ret < 0)
	        return new OutOI(null, ret);

	    while ( (p.get_first() == '+') || (p.get_first() == '-') ) {
	    	e1 = e0;
	    	
	    	ret_obj = parse_term(p);
		    ret = ret_obj.get_ret();
		    e2 = (AVExpr) ret_obj.get_obj();
	        if (ret < 0) {
	            e1.av_expr_free();
	            return new OutOI(null, ret);
	        }
	        e0 = new_eval_expr(ExprType.e_add, 1, e1, e2);
	        if (e0 == null) {
	            e1.av_expr_free();
	            e2.av_expr_free();
	            return new OutOI(null, Error.AVERROR(Error.ENOMEM));
	        }
	    };

	    return new OutOI(e0, 0);
	}

	private static OutOI parse_term(Parser p) {
	    int ret;
	    AVExpr e0, e1, e2;
	    

	    OutOI ret_obj = parse_factor(p);
	    ret = ret_obj.get_ret();
	    e0 = (AVExpr) ret_obj.get_obj();
	    if (ret < 0)
	        return new OutOI(null, ret);

	    while ( (p.get_first() == '*') || (p.get_first() == '/') ) {
	        char c = p.get_first();
	        p.next();
	        
	        e1 = e0;
	        
		    ret_obj = parse_factor(p);
		    ret = ret_obj.get_ret();
		    e2 = (AVExpr) ret_obj.get_obj();
		    if (ret < 0) {
	            e1.av_expr_free();
	            return new OutOI(null, ret);
	        }
	        e0 = new_eval_expr(c == '*' ? ExprType.e_mul : ExprType.e_div, 1, e1, e2);
	        if (e0 == null) {
	            e1.av_expr_free();
	            e2.av_expr_free();
	            return new OutOI(null, Error.AVERROR(Error.ENOMEM));
	        }
	    }
	    
	    return new OutOI(e0, 0);
	}
	

	private static OutOI parse_factor(Parser p) {
	    int sign, sign2, ret;
	    AVExpr e0, e1, e2;

	    OutOII ret_obj = parse_pow(p);
	    ret = ret_obj.get_ret();
	    e0 = (AVExpr) ret_obj.get_obj();
	    sign = ret_obj.get_val();
	    
	    if (ret < 0)
	        return new OutOI(null, ret);

	    while (p.get_first() == '^') {
	        e1 = e0;
	        p.next();

		    ret_obj = parse_pow(p);
		    ret = ret_obj.get_ret();
		    e2 = (AVExpr) ret_obj.get_obj();
		    sign2 = ret_obj.get_val();
	        
	        if (ret < 0) {
	            e1.av_expr_free();
	            return new OutOI(null, ret);
	        }
	        
	        e0 = new_eval_expr(ExprType.e_pow, 1, e1, e2);
	        if (e0 == null) {
	            e1.av_expr_free();
	            e2.av_expr_free();
	            return new OutOI(null, Error.AVERROR(Error.ENOMEM));
	        }

	        if (e0.get_param(1) != null){
	        	AVExpr tmp = e0.get_param(1);
	        	tmp.set_value(tmp.get_value() * (sign2 |1));
	        }

	    }
	    if (e0 != null)
	    	e0.set_value(e0.get_value() * (sign |1));

	    return new OutOI(e0, 0);
	}

	private static OutOII parse_pow(Parser p) {
	    int sign = ((p.get_first() == '+') ? 1 : 0) - ((p.get_first() == '-') ? 1 : 0);
	    
	    for (int i = 0 ; i < (sign & 1) ; i++ )
	    	p.next();
	    OutOI ret_obj = parse_primary(p);
	    int ret = ret_obj.get_ret();
	    AVExpr e = (AVExpr) ret_obj.get_obj();
	    
	    return new OutOII(e, sign, ret);
	}
	

	private static OutOI parse_primary(Parser p) {
	    AVExpr d = new AVExpr();
	    String next = p.get_s();
	    String s0 = p.get_s();
	    int ret, i;

	    /* number */
	    
	    OutDS ret_obj = av_strtod(p.get_s());
	    d.set_value(ret_obj.get_double());
	    next = ret_obj.get_string();
	    
	    if (!next.equals(p.get_s())) {
	        d.set_type(ExprType.e_value);
	        p.set_s(next);

    	    return new OutOI(d, 0);
	    }
	    d.set_value(1);

	    /* named constants */
	    for (i = 0 ; i < p.get_const_names().length ; i++) {
	    	if (strmatch(p.get_s(), p.get_const_name(i))) {
	            p.substr(p.get_const_name(i).length());
	            d.set_type(ExprType.e_const);
	            d.get_a().set_const_index(i);

        	    return new OutOI(d, 0);
	        }
	    }

	    p.set_s(UtilsString.strchr(p.get_s(), '('));
	    
	    if ( p.get_s() == null ) {
	        Log.av_log(p.get_log_ctx(), Log.AV_LOG_ERROR, "Undefined constant or missing '(' in '%s'\n", s0);
	        p.set_s(next);
	        
	        d.av_expr_free();
            return new OutOI(null, Error.AVERROR(Error.EINVAL));
	    }
	    p.next(); // "("
	    if (next.startsWith("(")) { // special case do-nothing
	        OutOI ret_obj2 = parse_expr(p);
	        ret = ret_obj2.get_ret();
	        d = (AVExpr) ret_obj2.get_obj();
	        
	        if (ret < 0)
	            return new OutOI(null, ret);
	        
	        if (!p.get_s().startsWith(")")) {
	        	Log.av_log(p.get_log_ctx(), Log.AV_LOG_ERROR, "Missing ')' in '%s'\n", s0);
	            
	            d.av_expr_free();	            
	            return new OutOI(null, Error.AVERROR(Error.EINVAL));
	        }
	        p.next(); // ")"

    	    return new OutOI(d, 0);
	    }
	    
	    OutOI ret_obj2 = parse_expr(p);
        ret = ret_obj2.get_ret();
        d.set_param(0, (AVExpr) ret_obj2.get_obj());
	    
	    
	    if (ret < 0) {
	        d.av_expr_free();
	        return new OutOI(null, ret);
	    }
	    
	    if (p.get_first() == ',') {
	        p.next(); // ","
	        
	        ret_obj2 = parse_expr(p);
	        ret = ret_obj2.get_ret();
	        d.set_param(1, (AVExpr) ret_obj2.get_obj());
	    }
	    
	    
	    if (p.get_first() != ')') {
	        Log.av_log(p.get_log_ctx(), Log.AV_LOG_ERROR, "Missing ')' or too many args in '%s'\n", s0);
	        
	        d.av_expr_free();
            return new OutOI(null, Error.AVERROR(Error.EINVAL));
	    }
	    p.next(); // ")"

	    d.set_type(ExprType.e_func0);
	    
	         if (strmatch(next, "sinh"  )) d.get_a().set_func0("sinh");
	    else if (strmatch(next, "cosh"  )) d.get_a().set_func0("cosh");
	    else if (strmatch(next, "tanh"  )) d.get_a().set_func0("tanh");
	    else if (strmatch(next, "sin"   )) d.get_a().set_func0("sin");
	    else if (strmatch(next, "cos"   )) d.get_a().set_func0("cos");
	    else if (strmatch(next, "tan"   )) d.get_a().set_func0("tan");
	    else if (strmatch(next, "atan"  )) d.get_a().set_func0("atan");
	    else if (strmatch(next, "asin"  )) d.get_a().set_func0("asin");
	    else if (strmatch(next, "acos"  )) d.get_a().set_func0("acos");
	    else if (strmatch(next, "exp"   )) d.get_a().set_func0("exp");
	    else if (strmatch(next, "log"   )) d.get_a().set_func0("log");
	    else if (strmatch(next, "abs"   )) d.get_a().set_func0("fabs");
	    else if (strmatch(next, "squish")) d.set_type(ExprType.e_squish);
	    else if (strmatch(next, "gauss" )) d.set_type(ExprType.e_gauss);
	    else if (strmatch(next, "mod"   )) d.set_type(ExprType.e_mod);
	    else if (strmatch(next, "max"   )) d.set_type(ExprType.e_max);
	    else if (strmatch(next, "min"   )) d.set_type(ExprType.e_min);
	    else if (strmatch(next, "eq"    )) d.set_type(ExprType.e_eq);
	    else if (strmatch(next, "gte"   )) d.set_type(ExprType.e_gte);
	    else if (strmatch(next, "gt"    )) d.set_type(ExprType.e_gt);
	    else if (strmatch(next, "lte"   )) { 
	    	AVExpr tmp = d.get_param(1); 
	    	d.set_param(1, d.get_param(0));
	    	d.set_param(0, tmp); 
	    	d.set_type(ExprType.e_gt);
	    } else if (strmatch(next, "lt"  )) {
	    	AVExpr tmp = d.get_param(1); 
	    	d.set_param(1, d.get_param(0));
	    	d.set_param(0, tmp); 
	    	d.set_type(ExprType.e_gte);
	    } else if (strmatch(next, "ld"  )) d.set_type(ExprType.e_ld);
	    else if (strmatch(next, "isnan" )) d.set_type(ExprType.e_isnan);
	    else if (strmatch(next, "st"    )) d.set_type(ExprType.e_st);
	    else if (strmatch(next, "while" )) d.set_type(ExprType.e_while);
	    else if (strmatch(next, "floor" )) d.set_type(ExprType.e_floor);
	    else if (strmatch(next, "ceil"  )) d.set_type(ExprType.e_ceil);
	    else if (strmatch(next, "trunc" )) d.set_type(ExprType.e_trunc);
	    else if (strmatch(next, "sqrt"  )) d.set_type(ExprType.e_sqrt);
	    else if (strmatch(next, "not"   )) d.set_type(ExprType.e_not);
	    else if (strmatch(next, "pow"   )) d.set_type(ExprType.e_pow);
	    else {
	        for (String func : p.get_func1_names()) {
	        	if (strmatch(next, func)) {
	        		d.get_a().set_func1(func);
	                d.set_type(ExprType.e_func1);

	        	    return new OutOI(d, 0);
	        	}
	        }
	        
	        for (String func : p.get_func2_names()) {
	        	if (strmatch(next, func)) {
	        		d.get_a().set_func2(func);
	                d.set_type(ExprType.e_func2);

	        	    return new OutOI(d, 0);
	        	}
	        }
	       
	        Log.av_log(p.get_log_ctx(), Log.AV_LOG_ERROR, "Unknown function in '%s'\n", s0);
	        
	        d.av_expr_free();	        
            return new OutOI(null, Error.AVERROR(Error.EINVAL));
	    }

	    return new OutOI(d, 0);
	}

	
	private static boolean strmatch(String s, String prefix) {
	    int i;
	    
	    for (i = 0 ; i < prefix.length() ; i++) {
	        if ( i >= s.length() || (prefix.charAt(i) != s.charAt(i)) ) 
	        	return false;
	    }
	    if (i > s.length())
	    	return !is_identifier_char(s.charAt(i));
	    else
	    	return true;
	}
	
	private static boolean is_identifier_char(char ch) {
		return Character.isLetterOrDigit(ch) || (ch == '_');
	}

	private static AVExpr new_eval_expr(ExprType type, int value, AVExpr p0,
			AVExpr p1) {
	    AVExpr e = new AVExpr();

	    e.set_type(type);
	    e.set_value(value);
	    e.set_param(0, p0);
	    e.set_param(1, p1);

	    return e;
	}

	private static boolean verify_expr(AVExpr e) {
	    if (e == null)
	    	return false;
	    
	    switch (e.get_type()) {
	        case e_value:
	        case e_const: 
	        	return true;
	        case e_func0:
	        case e_func1:
	        case e_squish:
	        case e_ld:
	        case e_gauss:
	        case e_isnan:
	        case e_floor:
	        case e_ceil:
	        case e_trunc:
	        case e_sqrt:
	        case e_not:
	            return verify_expr(e.get_param(0));
	        default: 
	        	return (verify_expr(e.get_param(0))) && (verify_expr(e.get_param(1)));
	    }
	}
}
