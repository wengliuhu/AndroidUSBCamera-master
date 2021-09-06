#version 100
uniform mat4 uMVPMatrix;
uniform mat4 uTexMatrix;
attribute highp vec4 aPosition;
attribute highp vec4 aTextureCoord;
varying highp vec2 vTextureCoord;
void main() {
 gl_Position = uMVPMatrix * aPosition;
 vTextureCoord = (uTexMatrix * aTextureCoord).xy;
}