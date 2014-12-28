#version 130

uniform sampler2D f_texture;
uniform vec4 f_color;
in vec2 f_uv;
out vec4 out_rgba;

void
main ()
{
  vec4 c = texture (f_texture, f_uv);
  out_rgba = c * f_color;
}
