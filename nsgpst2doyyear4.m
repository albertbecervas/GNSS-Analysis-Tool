function [doy, year4, hour] = nsgpst2doyyear4(time)


% Some declarations
year_seconds        = 365*24*60*60;
week_seconds        = 7*24*60*60;
day_seconds         = 24*60*60;

time = time/1e9;

%% year4
years_passed            = floor(time/year_seconds);
bis                     = years_passed/4;
year4                   = 1980 + floor(years_passed);

%% doy
% + 5 days since reference starts at doy = 6 (06/01/1980 00:00) (5 days
% from 01/01/1980 to 06/01/1980
% - leap days
time_corrected          = time + 5*day_seconds - (floor(bis))*day_seconds;
last_year               = mod(time_corrected, year_seconds);
doy                     = floor(last_year/day_seconds);
hour                    = (last_year/day_seconds - doy)*24;
minute                  = (hour - floor(hour))*60;
second                  = (minute - floor(minute))*60;

%% tow
tow = 0;

%% now
now = 0;


end