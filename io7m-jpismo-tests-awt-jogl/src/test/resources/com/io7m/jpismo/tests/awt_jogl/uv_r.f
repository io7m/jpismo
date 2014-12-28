#version 130

uniform sampler2D f_texture;
uniform vec4 f_color;
in vec2 f_uv;
out vec4 out_rgba;

void
main ()
{
  vec4 s = texture (f_texture, f_uv);
  vec3 b = vec3 (0.2, 0.2, 0.2);
  vec3 c = mix (b, f_color.rgb, s.r);
  out_rgba = vec4 (c, s.r);
}
