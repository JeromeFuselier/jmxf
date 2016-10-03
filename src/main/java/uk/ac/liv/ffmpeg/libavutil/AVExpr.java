package uk.ac.liv.ffmpeg.libavutil;

public class AVExpr {
	
	public static enum ExprType {
		e_value, e_const, e_func0, e_func1, e_func2,
        e_squish, e_gauss, e_ld, e_isnan,
        e_mod, e_max, e_min, e_eq, e_gt, e_gte,
        e_pow, e_mul, e_div, e_add,
        e_last, e_st, e_while, e_floor, e_ceil, e_trunc,
        e_sqrt, e_not,		
	};
	
	
	ExprType type;
	double value;
	
	AVExprUnion a = new AVExprUnion();
	
	
	AVExpr [] param = {null, null};

	public ExprType get_type() {
		return type;
	}
	
	public AVExprUnion get_a() {
		return a;
	}

	public void set_type(ExprType type) {
		this.type = type;
	}

	public double get_value() {
		return value;
	}

	public void set_value(double value) {
		this.value = value;
	}

	public AVExpr get_param(int i) {
		return param[i];
	}

	public void av_expr_free() {
		
	}

	public void set_param(int i, AVExpr p) {
		param[i] = p;
	} 
	
	
	
	
	
	
	
	
}
