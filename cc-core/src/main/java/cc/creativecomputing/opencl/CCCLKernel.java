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

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.common.os.Platform;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLException;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLMemory;
import com.jogamp.opencl.llb.CL;


/**
 * @author christianriekoff
 *
 */
public class CCCLKernel {

	private CLKernel _myKernel;
	private CCCLProgram _myProgram;
	
	private final ByteBuffer _myBuffer;
	
	CCCLKernel(CCCLProgram theProgram, CLKernel theKernel){
		_myProgram = theProgram;
		_myKernel = theKernel;
		
		_myBuffer = Buffers.newDirectByteBuffer((Platform.is32Bit()?4:8) * 16);
	}
	
	public CLKernel clKernel() {
		return _myKernel;
	}
	
	public void argument(int theArgumentIndex, CCCLTextureData theData) {
		argument(theArgumentIndex, theData.clImage());
	}
	
	public void argument(int theArgumentIndex, CLMemory<?> value) {
		_myBuffer.putLong(0, value.ID);
        setArgument(theArgumentIndex, Platform.is32Bit()?4:8, _myBuffer);
    }

    public void argument1i(int theArgumentIndex, int value) {
    	_myBuffer.putInt(0, value);
        setArgument(theArgumentIndex, 4, _myBuffer);
    }

    public void argument1l(int theArgumentIndex, long value) {
        if(isForce32BitArgsEnabled()) {
        	argument1i(theArgumentIndex, (int)value);
        	return;
        }
        _myBuffer.putLong(0, value);  
        setArgument(theArgumentIndex, 8, _myBuffer);
    }

    public void argument1f(int theArgumentIndex, float value) {
    	_myBuffer.putFloat(0, value);
        setArgument(theArgumentIndex, 4, _myBuffer);
    }

    public void argument2f(int theArgumentIndex, float value1, float value2) {
    	_myBuffer.putFloat(0, value1);
    	_myBuffer.putFloat(4, value2);
        setArgument(theArgumentIndex, 8, _myBuffer);
    }

    public void argument3f(int theArgumentIndex, float theValue1, float theValue2, float theValue3) {
    	argument4f(theArgumentIndex, theValue1, theValue2, theValue3,0);
    }

    public void argument4f(int theArgumentIndex, float theValue1, float theValue2, float theValue3, float theValue4) {
    	_myBuffer.putFloat(0, theValue1);
    	_myBuffer.putFloat(4, theValue2);
    	_myBuffer.putFloat(8, theValue3);
    	_myBuffer.putFloat(12, theValue4);
        setArgument(theArgumentIndex, 16, _myBuffer);
    }
    
    public void argumentNf(int theArgumentIndex, float...theValues){
    	for(int i = 0; i < theValues.length;i++){
    		_myBuffer.putFloat(i * 4, theValues[i]);
    	}
        setArgument(theArgumentIndex, theValues.length * 4, _myBuffer);
    }

    public void argument1d(int theArgumentIndex, double value) {
        if(isForce32BitArgsEnabled()) {
            argument1f(theArgumentIndex, (float)value);
            return;
        }
        
        _myBuffer.putDouble(0, value);
        setArgument(theArgumentIndex, 8, _myBuffer);
    }

    public void nullArgument(int theArgumentIndex, int size) {
        setArgument(theArgumentIndex, size, null);
    }

    public void setArgs(CLMemory<?>... values) {
        setArgs(0, values);
    }

    private void setArgs(int startIndex, CLMemory<?>... values) {
        for (int i = 0; i < values.length; i++) {
            argument(i+startIndex, values[i]);
        }
    }

    private void setArgument(int theArgumentIndex, int size, Buffer value) {
        if(theArgumentIndex >= _myKernel.numArgs || theArgumentIndex < 0) {
            throw new IndexOutOfBoundsException("kernel "+ this +" has "+_myKernel.numArgs+" arguments, can not set argument with index "+theArgumentIndex);
        }
        if(!_myProgram.isExecutable()) {
            throw new IllegalStateException("can not set program" + " arguments for a not executable program. " + _myProgram);
        }

        int ret = _myKernel.getContext().getCL().clSetKernelArg(_myKernel.ID, theArgumentIndex, size, value);
        if(ret != CL.CL_SUCCESS) {
            throw CLException.newException(ret, "error setting arg "+theArgumentIndex+" to value "+value+" of size "+size+" of "+this);
        }
    }

    /**
     * Forces double and long arguments to be passed as float and int to the OpenCL kernel.
     * This can be used in applications which want to mix kernels with different floating point precision.
     */
    public void setForce32BitArgs(boolean force) {
        _myKernel.setForce32BitArgs(force);
    }

    /**
     * @see #setForce32BitArgs(boolean) 
     */
    public boolean isForce32BitArgsEnabled() {
        return _myKernel.isForce32BitArgsEnabled();
    }

    /**
     * Returns the amount of local memory in bytes being used by a kernel.
     * This includes local memory that may be needed by an implementation to execute the kernel,
     * variables declared inside the kernel with the <code>__local</code> address qualifier and local memory
     * to be allocated for arguments to the kernel declared as pointers with the <code>__local</code> address
     * qualifier and whose size is specified with clSetKernelArg.
     * If the local memory size, for any pointer argument to the kernel declared with
     * the <code>__local</code> address qualifier, is not specified, its size is assumed to be 0.
     */
    public long localMemorySize(CLDevice theDevice) {
        return _myKernel.getLocalMemorySize(theDevice);
    }

    /**
     * Returns the work group size for this kernel on the given device.
     * This provides a mechanism for the application to query the work-group size
     * that can be used to execute a kernel on a specific device given by device.
     * The OpenCL implementation uses the resource requirements of the kernel
     * (register usage etc.) to determine what this work-group size should be. 
     */
    public long workGroupSize(CLDevice theDevice) {
        return _myKernel.getWorkGroupSize(theDevice);
    }

    /**
     * Returns the work-group size specified by the <code>__attribute__((reqd_work_group_size(X, Y, Z)))</code> qualifier in kernel sources.
     * If the work-group size is not specified using the above attribute qualifier <code>new long[]{(0, 0, 0)}</code> is returned.
     * The returned array has always three elements.
     */
    public long[] compileWorkGroupSize(CLDevice theDevice) {
        return _myKernel.getCompileWorkGroupSize(theDevice);
    }

    /**
     * Releases all resources of this kernel from its context.
     */
    public void release() {
        _myKernel.release();
    }

    @Override
    public String toString() {
        return "CLKernel [id: " + _myKernel.ID + " name: " + _myKernel.name+"]";
    }

    @Override
    public boolean equals(Object obj) {
        return _myKernel.equals(obj);
    }

    @Override
    public int hashCode() {
        return _myKernel.hashCode();
    }
}
