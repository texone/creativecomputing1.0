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
package cc.creativecomputing.control;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*; 
import static java.lang.annotation.RetentionPolicy.*; 

@Target({FIELD,METHOD})
@Retention(RUNTIME)
public @interface CCControl {
	String name() default "";
	String tabName() default "";
	
	String defaultValue() default "";
	
	int column() default -1;
	
	int x() default -1;
	int y() default -1;
	
	int width() default -1;
	int height() default -1;
	
	float min() default 0;
	float max() default 1;
	
	float minX() default 0;
	float minY() default 0;
	float minZ() default 0;
	
	float maxX() default 1;
	float maxY() default 1;
	float maxZ() default 1;
	
	boolean toggle() default true;
	
	boolean accumulate() default false;
	boolean external() default false;
	
	// EVELOPE RELATED ATTRIBUTES
	
	float length() default 2;
	
	int numberOfEnvelopes() default 1;
}
