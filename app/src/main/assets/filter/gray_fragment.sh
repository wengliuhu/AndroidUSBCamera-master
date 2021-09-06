#version 100
#extension GL_OES_EGL_image_external : require
precision highp float;
uniform samplerExternalOES sTexture;
varying highp vec2 vTextureCoord;
const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);
void main() {
    vec4 mask = texture2D(sTexture, vTextureCoord);
    float luminance = dot(mask.rgb, W);
    gl_FragColor = vec4(vec3(luminance), 1.0);
}

