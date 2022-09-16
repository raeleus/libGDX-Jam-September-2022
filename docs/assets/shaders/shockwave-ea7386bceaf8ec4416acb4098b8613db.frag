// Originally based on
// https://www.shadertoy.com/view/wsGSR1

#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture0;

uniform float u_time;
uniform float u_duration;
uniform float u_radius;
uniform float u_thickness;
uniform vec2 u_viewport;
uniform vec2 u_position;

void main()
{
    float time = min(u_time, u_duration);
    float radius = u_radius * time * time;

    float time_ratio = time/u_duration;
   	float shockwave = smoothstep(radius, radius-2.0, length((u_position - v_texCoords) * u_viewport));
    shockwave *= smoothstep((radius-2.)*u_thickness, radius-2.0,length((u_position - v_texCoords) * u_viewport));
    shockwave *= 1.-time_ratio;

    vec2 disp_dir = v_texCoords - u_position;

    vec3 col = texture2D(u_texture0, v_texCoords + 0.02*disp_dir*shockwave).rgb;

    gl_FragColor = vec4(col,1.0);
}