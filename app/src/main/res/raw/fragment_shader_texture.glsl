precision mediump float;
uniform vec4 vColor; 
uniform sampler2D u_Texture;    // The input texture.
varying vec2 v_TexCoordinate; // Interpolated texture coordinate per fragment.

void main() 
{ 
	//gl_FragColor = vec4(texture2D(u_Texture, v_TexCoordinate).xyz, texture2D(u_Texture, v_TexCoordinate).w * 0.5);
	gl_FragColor =  texture2D(u_Texture, v_TexCoordinate); 
}