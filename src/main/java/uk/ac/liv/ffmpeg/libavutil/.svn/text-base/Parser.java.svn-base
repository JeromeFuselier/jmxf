package uk.ac.liv.ffmpeg.libavutil;

public class Parser {
	
	public static final int VARS = 10;
	
	AVClass av_class;
    int stack_index;
    String s;
    double [] const_values;
    String [] const_names;          // NULL terminated
    //double (* const *funcs1)(void *, double a);           // NULL terminated
    String [] func1_names;          // NULL terminated
    //double (* const *funcs2)(void *, double a, double b); // NULL terminated
    String [] func2_names;          // NULL terminated
    Object opaque;
    int log_offset;
    String log_ctx;

    double [] var = new double[VARS];

	public AVClass get_class() {
		return av_class;
	}

	public void set_class(AVClass av_class) {
		this.av_class = av_class;
	}

	public int get_stack_index() {
		return stack_index;
	}

	public void set_stack_index(int stack_index) {
		this.stack_index = stack_index;
	}

	public String get_s() {
		return s;
	}

	public void set_s(String s) {
		this.s = s;
	}

	public double [] get_const_values() {
		return const_values;
	}

	public void set_const_values(double [] const_values) {
		this.const_values = const_values;
	}

	public String[] get_const_names() {
		return const_names;
	}

	public String get_const_name(int i) {
		return const_names[i];
	}

	public void set_const_names(String[] const_names) {
		this.const_names = const_names;
	}

	public String[] get_func1_names() {
		return func1_names;
	}

	public String get_func1_name(int i) {
		return func1_names[i];
	}
	public void set_func1_names(String[] func1_names) {
		this.func1_names = func1_names;
	}

	public String[] get_func2_names() {
		return func2_names;
	}

	public void set_func2_names(String[] func2_names) {
		this.func2_names = func2_names;
	}

	public Object get_opaque() {
		return opaque;
	}

	public void set_opaque(Object opaque) {
		this.opaque = opaque;
	}

	public int get_log_offset() {
		return log_offset;
	}

	public void set_log_offset(int log_offset) {
		this.log_offset = log_offset;
	}

	public String get_log_ctx() {
		return log_ctx;
	}

	public void set_log_ctx(String log_ctx) {
		this.log_ctx = log_ctx;
	}

	public double[] get_var() {
		return var;
	}

	public void set_var(double[] var) {
		this.var = var;
	}

	public char get_first() {
		if (s.length() > 0)
			return s.charAt(0);
		else
			return 0;
	}

	public void next() {
		s = s.substring(1);
	}
	
	public void substr(int l) {
		s = s.substring(l);
	}
    
    
    

}
