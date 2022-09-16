// Originally based on
// https://www.shadertoy.com/view/ws2BDm

#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture0;

uniform vec2 u_pixelSize;
uniform vec2 u_viewport;

void main()
{
    vec3 c;
    vec3 src = texture2D(u_texture0, v_texCoords).rgb;
    vec3 sum;
    vec2 fragCoord = v_texCoords * u_viewport;

    for(int a = 0; a < int(u_pixelSize.x); a++) {
        for(int b = 0; b < int(u_pixelSize.y); b++) {
            c = texture2D(u_texture0, (fragCoord - mod(fragCoord, u_pixelSize) + vec2(vec2(a, b))) / u_viewport.xy).rgb;
            sum += c;
        }
    }
    sum /= u_pixelSize.x * u_pixelSize.y;
    gl_FragColor.rgb = sum;
}
