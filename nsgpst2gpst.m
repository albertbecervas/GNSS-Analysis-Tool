function [tow, now] = nsgpst2gpst(time)

% Some declarations
year_seconds        = 365*24*60*60;
week_seconds        = 7*24*60*60;
day_seconds         = 24*60*60;

time = time/1e9;

%% now
now                 = floor(time/week_seconds);

%% tow
tow                 = round(mod(time, week_seconds));

end