/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.opencl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import cc.creativecomputing.io.CCIOException;
import cc.creativecomputing.io.CCIOUtil;

import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;
import com.jogamp.opencl.CLProgram.Status;
import com.jogamp.opencl.util.CLBuildListener;

/**
 * @author christianriekoff
 *
 */
public class CCCLProgram {
	
	/**
     * Treat double precision floating-point constant as single precision constant.
     */
    public final static String SINGLE_PRECISION_CONSTANTS = "-cl-single-precision-constant";

    /**
     * This option controls how single precision and double precision denormalized numbers are handled.
     * If specified as a build option, the single precision denormalized numbers may be flushed to zero
     * and if the optional extension for double precision is supported, double precision denormalized numbers
     * may also be flushed to zero. This is intended to be a performance hint and the OpenCL compiler can choose
     * not to flush denorms to zero if the device supports single precision (or double precision) denormalized numbers.<br>
     * This option is ignored for single precision numbers if the device does not support single precision denormalized
     * numbers i.e. {@link CLDevice.FPConfig#DENORM} is not present in the set returned by {@link CLDevice#getSingleFPConfig()}<br>
     * This option is ignored for double precision numbers if the device does not support double precision or if it does support
     * double precision but {@link CLDevice.FPConfig#DENORM} is not present in the set returned by {@link CLDevice#getDoubleFPConfig()}.<br>
     * This flag only applies for scalar and vector single precision floating-point variables and computations on
     * these floating-point variables inside a program. It does not apply to reading from or writing to image objects.
     */
    public final static String DENORMS_ARE_ZERO = "-cl-denorms-are-zero";

    /**
     * This option disables all optimizations. The default is optimizations are enabled.
     */
    public final static String DISABLE_OPT = "-cl-opt-disable";

    /**
     * This option allows the compiler to assume the strictest aliasing rules.
     */
    public final static String STRICT_ALIASING = "-cl-strict-aliasing";

    /**
     * Allow a * b + c to be replaced by a mad. The mad computes a * b + c with reduced accuracy.
     * For example, some OpenCL devices implement mad as truncate the result of a * b before adding it to c.
     */
    public final static String ENABLE_MAD = "-cl-mad-enable";

    /**
     * Allow optimizations for floating-point arithmetic that ignore the signedness of zero.
     * IEEE 754 arithmetic specifies the behavior of distinct +0.0 and -0.0 values, which then prohibits
     * simplification of expressions such as x+0.0 or 0.0*x (even with -cl-finite-math-only ({@link #FINITE_MATH_ONLY})).
     * This option implies that the sign of a zero result isn't significant.
     */
    public final static String NO_SIGNED_ZEROS = "-cl-no-signed-zeros";

    /**
     * Allow optimizations for floating-point arithmetic that<br>
     * (a) assume that arguments and results are valid,<br>
     * (b) may violate IEEE 754 standard and<br>
     * (c) may violate the OpenCL numerical compliance requirements as defined in section
     * 7.4 for single-precision floating-point, section 9.3.9 for double-precision floating-point,
     * and edge case behavior in section 7.5.
     * This option includes the -cl-no-signed-zeros ({@link #NO_SIGNED_ZEROS})
     * and -cl-mad-enable ({@link #ENABLE_MAD}) options.
     */
    public final static String UNSAFE_MATH = "-cl-unsafe-math-optimizations";

    /**
     * Allow optimizations for floating-point arithmetic that assume that arguments and results are not NaNs or ?.
     * This option may violate the OpenCL numerical compliance requirements defined in in section 7.4 for
     * single-precision floating-point, section 9.3.9 for double-precision floating-point, and edge case behavior in section 7.5.
     */
    public final static String FINITE_MATH_ONLY = "-cl-finite-math-only";

    /**
     * Sets the optimization options -cl-finite-math-only ({@link #FINITE_MATH_ONLY}) and -cl-unsafe-math-optimizations ({@link #UNSAFE_MATH}).
     * This allows optimizations for floating-point arithmetic that may violate the IEEE 754
     * standard and the OpenCL numerical compliance requirements defined in the specification
     * in section 7.4 for single-precision floating-point, section 9.3.9 for double-precision
     * floating-point, and edge case behavior in section 7.5. This option causes the preprocessor
     * macro __FAST_RELAXED_MATH__ to be defined in the OpenCL program.
     */
    public final static String FAST_RELAXED_MATH = "-cl-fast-relaxed-math";

    /**
     * Inhibit all warning messages.
     */
    public final static String DISABLE_WARNINGS = "-w";

    /**
     * Make all warnings into errors.
     */
    public final static String WARNINGS_ARE_ERRORS = "-Werror";
	
	public static class CCCLProgramBuildAttributes{
		private CLBuildListener _myListener;
		private String _myOptions;
		private CLDevice[] _myDevices;
		
		public CCCLProgramBuildAttributes() {
			
		}
		
		public CCCLProgramBuildAttributes(String...theOptions) {
			options(theOptions);
		}
		
		public void listener(CLBuildListener theListener) {
			_myListener = theListener;
		}
		
		public void options(String theOptions) {
			_myOptions = theOptions;
		}
		
		public void options(String...theOptions) {
			_myOptions = CLProgram.optionsOf(theOptions);
		}
		
		public void devices(CLDevice...theDevices) {
			_myDevices = theDevices;
		}
	}

	private CLProgram _myProgram;
	private CCOpenCL _myOpenCL;
	private StringBuffer _myCLCode = new StringBuffer();
	
	public CCCLProgram(CCOpenCL theOpenCL) {
		_myOpenCL = theOpenCL;
	}
	
	public void appendSource(String theFile) {
		appendSource(CCOpenCL.class, theFile);
	}
	
	public void appendSource(Class<?> theClass, String theFile) {
		try {
			BufferedReader myReader = CCIOUtil.createReader(theClass.getResourceAsStream(theFile));
			String myLine;
			while ((myLine = myReader.readLine()) != null) {
				_myCLCode.append(myLine);
				_myCLCode.append("\n");
			}
		} catch (IOException e) {
			throw new CCIOException(e);
		} catch(NullPointerException e) {
			throw new CCIOException("Could not load file:" + theClass.getResource("") + theFile);
		}
	}
	
	public void build() {
		build(new CCCLProgramBuildAttributes());
	}
	
	public void build(CCCLProgramBuildAttributes theAttributes) {
		_myProgram = _myOpenCL.context().createProgram(_myCLCode.toString());
		
		_myProgram.build(
			theAttributes._myListener, 
			theAttributes._myOptions, 
			theAttributes._myDevices
		);
	}
	
	/**
	 * Creates a kernel with the specified kernel name.
	 * @param theKernelName name of the kernel to create
	 * @return the kernel object
	 */
	public CCCLKernel createKernel(String theKernelName) {
		CLKernel myKernel = _myProgram.createCLKernel(theKernelName);
		return new CCCLKernel(this, myKernel);
	}

    /**
     * Returns all devices associated with this program.
     * @return devices associated with this program.
     */
    public CLDevice[] devices() {
        return _myProgram.getCLDevices();
    }

    /**
     * Returns the build log of this program on all devices. The contents of the log are
     * implementation dependent.
     * @return build log of this program
     */
    public String buildLog() {
        return _myProgram.getBuildLog();
    }

    /**
     * Returns the build status enum of this program for each device as Map.
     * @return build status
     */
    public Map<CLDevice,Status> getBuildStatus() {
        return _myProgram.getBuildStatus();
    }

    /**
     * Returns true if the build status 'BUILD_SUCCESS' for at least one device
     * of this program exists.
     * @return if this program is executable
     */
    public boolean isExecutable() {
        return _myProgram.isExecutable();
    }

    /**
     * Returns the build log for this program on the specified device. The contents
     * of the log are implementation dependent log can be an empty String.
     * @param theDevice specified device
     * @return build log for this program on the specified device
     */
    public String buildLog(CLDevice theDevice) {
        return _myProgram.getBuildLog(theDevice);
    }

    /**
     * Returns the build status enum for this program on the specified device.
     * @param theDevice specified device
     * @return build status enum for this program on the specified device
     */
    public Status buildStatus(CLDevice theDevice) {
        return _myProgram.getBuildStatus(theDevice);
    }

    /**
     * Returns the source code of this program. Note: sources are not cached,
     * each call of this method calls into Open
     * @return source code of this program
     */
    public String source() {
        return _myProgram.getSource();
    }

    /**
     * Returns the binaries for this program in an ordered Map containing the device as key
     * and the program binaries as value.
     * @return binaries for this program
     */
    public Map<CLDevice, byte[]> binaries() {
    	return _myProgram.getBinaries();
    }
	
    /**
     * Releases this program with its kernels.
     */
	public void release() {
		_myProgram.release();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _myProgram.toString();
	}
}
