precision highp float;
uniform sampler2D Texture;
varying vec2 TextureCoordsVarying;
//灰度计算比率 (借用GPUImage的值)
const highp vec3 ratio = vec3(0.2125, 0.7154, 0.0721);
void main (void) {
    vec4 mask = texture2D(Texture, TextureCoordsVarying);
    // Gray值
    float luminance = dot(mask.rgb, ratio);
    gl_FragColor = vec4(vec3(luminance), 1.0);
}
