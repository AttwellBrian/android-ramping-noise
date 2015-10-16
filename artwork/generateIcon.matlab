function y = ss(x) 
  y = sin(x) * x
end
x=0:.1:25;
plot(x,arrayfun(@ss,x),"linewidth", 12, 'Color',[0xFF/0xFF,0x40/0xFF,0x81/0xFF])
axis off;
set(gcf,'Color',[0x35/0xFF,0x51/0xFF,0xB5/0xFF])
