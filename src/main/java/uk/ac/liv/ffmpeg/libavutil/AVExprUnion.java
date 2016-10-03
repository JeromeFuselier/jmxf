package uk.ac.liv.ffmpeg.libavutil;

public class AVExprUnion {
	
	int const_index;
    
	String func0;
	String func1;
	String func2;
	
	
	public int get_const_index() {
		return const_index;
	}

	public void set_const_index(int const_index) {
		this.const_index = const_index;
	}

	double func0(double d) {
			
		if (func0.equals("sinh")) 
			return Math.sinh(d);
	    else if (func0.equals("cosh"))
    		return Math.cosh(d);
	    else if (func0.equals("tanh"))
    		return Math.tanh(d);
	    else if (func0.equals("sin"))
    		return Math.sin(d);
	    else if (func0.equals("cos"))
    		return Math.cos(d);
	    else if (func0.equals("tan"))
    		return Math.tan(d);
	    else if (func0.equals("atan"))
    		return Math.atan(d);
	    else if (func0.equals("asin"))
    		return Math.asin(d);
	    else if (func0.equals("acos"))
    		return Math.acos(d);
	    else if (func0.equals("exp"))
    		return Math.exp(d);
	    else if (func0.equals("log"))
    		return Math.log(d);
	    else if (func0.equals("fabs"))
    		return Math.abs(d);
			
		return 0;
	}
	
    double func1(Object o, double d) {
    	return 0;
    }
    
    double func2(Object o, double d1, double d2) {
    	return 0;
    }

	public void set_func0(String func0) {
		this.func0 = func0;
	}

	public void set_func1(String func1) {
		this.func1 = func1;
	}

	public void set_func2(String func2) {
		this.func2 = func2;
	}

}
