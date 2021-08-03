attribute vec4 vPosition;
uniform mat4 uMVPMatrix
varying  vec4 vColor
attribute vec4 aColor
void main() {
  gl_Position = uMVPMatrix*vPosition;
  vColor=aColor;
}