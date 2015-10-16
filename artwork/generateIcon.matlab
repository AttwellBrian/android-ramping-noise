function y = ss(x) 
  y = sin(x) * x
end
x=0:.1:30;
plot(x,arrayfun(@ss,x),"linewidth", 6, 'Color',[0xFF/0xFF,40.0/0xFF,81.0/0xFF])
axis off;
set(gcf,'Color',[0,0.7,0.9])
