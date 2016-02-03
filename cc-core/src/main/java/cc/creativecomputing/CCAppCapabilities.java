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
package cc.creativecomputing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2GL3;

/**
 * This class collects all of the settings for a specific {@link javax.media.opengl.GLContext}, avoiding unnecessary
 * communications with the graphics hardware for settings which won't change.
 * 
 * @author Christian Riekoff
 */
public final class CCAppCapabilities {

	private static final IntBuffer intBuf = IntBuffer.allocate(16);

	// TODO Due to JOGL buffer check, you can't use smaller sized
	// buffers (min_size = 16) for glGetFloat().
	private static final FloatBuffer floatBuf = FloatBuffer.allocate(16);

	public static boolean GL_VERSION_1_1;

	public static boolean GL_VERSION_1_2;

	public static boolean GL_VERSION_1_3;

	public static boolean GL_VERSION_1_4;

	public static boolean GL_VERSION_1_5;

	public static boolean GL_VERSION_2_0;

	public static boolean GL_VERSION_2_1;

	public static boolean GL_VERSION_3_0;

	public static boolean GL_ARB_imaging;

	public static boolean GL_EXT_blend_func_separate;

	public static boolean GL_EXT_blend_equation_separate;

	public static boolean GL_EXT_blend_minmax;

	public static boolean GL_EXT_blend_subtract;

	public static boolean GL_ARB_depth_texture;

	public static boolean GL_EXT_fog_coord;

	public static boolean GL_EXT_compiled_vertex_array;

	public static boolean GL_ARB_fragment_program;

	public static boolean GL_ARB_shader_objects;

	public static boolean GL_ARB_fragment_shader;

	public static boolean GL_ARB_vertex_shader;

	public static boolean GL_ARB_shading_language_100;

	public static boolean GL_EXT_stencil_two_side;

	public static boolean GL_EXT_stencil_wrap;

	public static boolean GL_ARB_multitexture;

	public static boolean GL_ARB_texture_env_dot3;

	public static boolean GL_ARB_texture_env_combine;

	public static boolean GL_SGIS_generate_mipmap;

	public static boolean GL_ARB_vertex_program;

	public static boolean GL_ARB_texture_mirrored_repeat;

	public static boolean GL_EXT_texture_mirror_clamp;

	public static boolean GL_ARB_texture_border_clamp;

	public static boolean GL_EXT_texture_compression_s3tc;

	public static boolean GL_EXT_texture_3d;

	public static boolean GL_EXT_generate_mipmap;

	public static boolean GL_ARB_texture_cube_map;

	public static boolean GL_EXT_texture_filter_anisotropic;

	public static boolean GL_ARB_texture_non_power_of_two;

	public static boolean GL_ARB_texture_rectangle;
	
	public static boolean GL_EXT_abgr;

	public static int GL_MAX_TEXTURE_UNITS;

	public static int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB;

	public static int GL_MAX_TEXTURE_IMAGE_UNITS_ARB;

	public static int GL_MAX_TEXTURE_COORDS_ARB;

	public static float GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;

	public static int GL_MAX_VERTEX_ATTRIBS_ARB;

	public static int GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB;

	public static int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB;

	public static int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB;

	public static int GL_MAX_VARYING_FLOATS_ARB;

	public static String GL_SHADING_LANGUAGE_VERSION_ARB;

	public static boolean GL_ARB_vertex_buffer_object;

	public static boolean GL_ARB_shadow;
	
	
	// framebuffer object
	public static boolean GL_EXT_framebuffer_object;
	
	public static boolean GL_ARB_draw_buffers;
	
	public static boolean GL_EXT_framebuffer_multisample;
	
	public static boolean GL_EXT_framebuffer_blit;
	
	public static boolean GL_NV_framebuffer_multisample_coverage;

	public static void init(final GL2GL3 gl) {
		// See Renderer
		GL_ARB_vertex_buffer_object = gl.isExtensionAvailable("GL_ARB_vertex_buffer_object");
		GL_VERSION_1_1 = gl.isExtensionAvailable("GL_VERSION_1_1");
		GL_VERSION_1_2 = gl.isExtensionAvailable("GL_VERSION_1_2");
		GL_VERSION_1_3 = gl.isExtensionAvailable("GL_VERSION_1_3");
		GL_VERSION_1_4 = gl.isExtensionAvailable("GL_VERSION_1_4");
		GL_VERSION_1_5 = gl.isExtensionAvailable("GL_VERSION_1_5");
		GL_VERSION_2_0 = gl.isExtensionAvailable("GL_VERSION_2_0");
		GL_VERSION_2_1 = gl.isExtensionAvailable("GL_VERSION_2_1");
		GL_VERSION_3_0 = gl.isExtensionAvailable("GL_VERSION_3_0");

		// See BlendState
		GL_ARB_imaging = gl.isExtensionAvailable("GL_ARB_imaging");
		GL_EXT_blend_func_separate = gl.isExtensionAvailable("GL_EXT_blend_func_separate");
		GL_EXT_blend_equation_separate = gl.isExtensionAvailable("GL_EXT_blend_equation_separate");
		GL_EXT_blend_minmax = gl.isExtensionAvailable("GL_EXT_blend_minmax");
		GL_EXT_blend_subtract = gl.isExtensionAvailable("GL_EXT_blend_subtract");

		// See FogState
		GL_EXT_fog_coord = gl.isExtensionAvailable("GL_EXT_fog_coord");

		// See FragmentProgramState
		GL_ARB_fragment_program = gl.isExtensionAvailable("GL_ARB_fragment_program");

		// See ShaderObjectsState
		GL_ARB_shader_objects = gl.isExtensionAvailable("GL_ARB_shader_objects");
		GL_ARB_fragment_shader = gl.isExtensionAvailable("GL_ARB_fragment_shader");
		GL_ARB_vertex_shader = gl.isExtensionAvailable("GL_ARB_vertex_shader");
		GL_ARB_shading_language_100 = gl.isExtensionAvailable("GL_ARB_shading_language_100");
		if (GL_ARB_shading_language_100) {
			GL_SHADING_LANGUAGE_VERSION_ARB = gl.glGetString(GL2GL3.GL_SHADING_LANGUAGE_VERSION);
		} else {
			GL_SHADING_LANGUAGE_VERSION_ARB = "";
		}

		// See TextureState
		GL_ARB_depth_texture = gl.isExtensionAvailable("GL_ARB_depth_texture");
		GL_ARB_shadow = gl.isExtensionAvailable("GL_ARB_shadow");

		gl.glGetIntegerv(GL2GL3.GL_MAX_VERTEX_ATTRIBS, intBuf);
		GL_MAX_VERTEX_ATTRIBS_ARB = intBuf.get(0);
		gl.glGetIntegerv(GL2GL3.GL_MAX_VERTEX_UNIFORM_COMPONENTS, intBuf);
		GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB = intBuf.get(0);
		gl.glGetIntegerv(GL2GL3.GL_MAX_VARYING_FLOATS, intBuf);
		GL_MAX_VARYING_FLOATS_ARB = intBuf.get(0);
		gl.glGetIntegerv(GL2GL3.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, intBuf);
		GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB = intBuf.get(0);
		gl.glGetIntegerv(GL2GL3.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, intBuf);
		GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB = intBuf.get(0);
		gl.glGetIntegerv(GL2GL3.GL_MAX_TEXTURE_IMAGE_UNITS, intBuf);
		GL_MAX_TEXTURE_IMAGE_UNITS_ARB = intBuf.get(0);
		gl.glGetIntegerv(GL2GL3.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, intBuf);
		GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB = intBuf.get(0);
		

		if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
			gl.glGetFloatv(GL2GL3.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, floatBuf);
			GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = floatBuf.get(0);
		} else {
			GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0;
		}
		// See StencilState
		GL_EXT_stencil_two_side = gl.isExtensionAvailable("GL_EXT_stencil_two_side");
		GL_EXT_stencil_wrap = gl.isExtensionAvailable("GL_EXT_stencil_wrap");
		GL_ARB_texture_env_dot3 = gl.isExtensionAvailable("GL_ARB_texture_env_dot3");
		GL_ARB_texture_env_combine = gl.isExtensionAvailable("GL_ARB_texture_env_combine");
		GL_SGIS_generate_mipmap = gl.isExtensionAvailable("GL_SGIS_generate_mipmap");
		GL_EXT_texture_compression_s3tc = gl.isExtensionAvailable("GL_EXT_texture_compression_s3tc");
		GL_EXT_texture_3d = gl.isExtensionAvailable("GL_EXT_texture_3d");
		GL_EXT_generate_mipmap = gl.isExtensionAvailable("GL_EXT_generate_mipmap");
		GL_EXT_abgr = gl.isExtensionAvailable("GL_EXT_abgr");
		GL_ARB_texture_cube_map = gl.isExtensionAvailable("GL_ARB_texture_cube_map");
		GL_EXT_texture_filter_anisotropic = gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic");
		GL_ARB_texture_non_power_of_two = gl.isExtensionAvailable("GL_ARB_texture_non_power_of_two");
		GL_ARB_texture_rectangle = gl.isExtensionAvailable("GL_ARB_texture_rectangle");
		// See VertexProgram
		GL_ARB_vertex_program = gl.isExtensionAvailable("GL_ARB_vertex_program");

		// See TextureStateRecord
		GL_ARB_texture_mirrored_repeat = gl.isExtensionAvailable("GL_ARB_texture_mirrored_repeat");
		GL_EXT_texture_mirror_clamp = gl.isExtensionAvailable("GL_EXT_texture_mirror_clamp");
		GL_ARB_texture_border_clamp = gl.isExtensionAvailable("GL_ARB_texture_border_clamp");

		GL_EXT_compiled_vertex_array = gl.isExtensionAvailable("GL_EXT_compiled_vertex_array");
		
		// Framebuffer check
		GL_EXT_framebuffer_object = gl.isExtensionAvailable("GL_EXT_framebuffer_object");
		GL_ARB_draw_buffers = gl.isExtensionAvailable("GL_ARB_draw_buffers");
		GL_EXT_framebuffer_multisample = gl.isExtensionAvailable("GL_EXT_framebuffer_multisample");
		GL_EXT_framebuffer_blit = gl.isExtensionAvailable("GL_EXT_framebuffer_blit");
		GL_NV_framebuffer_multisample_coverage = gl.isExtensionAvailable("GL_NV_framebuffer_multisample_coverage");
	}

}
