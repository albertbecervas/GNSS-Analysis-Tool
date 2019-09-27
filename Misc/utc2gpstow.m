function [tow, gpsweek, dow, doy] = utc2gpstow(utc, leapsec)

%------------------------------------------------------------------------------%
%                                                                              %
%  [tow, gpsweek, dow, doy] = utc2gpstow(utc, leapsec)                         %
%                                                                              %
%  Calculate GPS time-of-week given a UTC date vector                          %
%  David S. De Lorenzo                                                         %
%  07 October 2009                                                             %
%                                                                              %
%  Change History:                                                             %
%     03/15/10 : DSD - added GPS week output variable                          %
%     08/14/15 : DSD - added day of week (0-6) and day of year (1+) outputs    %
%     10/01/15 : DSD - added input checking and end-of-week rollover recovery  %
%                                                                              %
%------------------------------------------------------------------------------%
%                                                                              %
%  Returns the GPS time-of-week in seconds given a 6-element UTC date          %
%  vector [yyyy mm dd HH MM SS] and the number of leap seconds between         %
%  UTC and GPS time.  The input date should fall on or after Jan 06, 1980.     %
%      yyyy - 4-digit year                                                     %
%       mm  - month number (1-12)                                              %
%       dd  - day number (1-31)                                                %
%       HH  -  hour number  (0-23)                                             %
%       MM  - whole minutes (0-59)                                             %
%       SS  - seconds (0-59.999999999)                                         %
%  Also outputs gps week number, day of week (0-6), and day of year (1-365).   %
%                                                                              %
%  References:                                                                 %
%      GPS, UTC, and TAI Clocks (very useful):                                 %
%        http://www.leapsecond.com/java/gpsclock.htm                           %
%      CORS GPS Calendar:                                                      %
%         http://www.ngs.noaa.gov/CORS/Gpscal.shtml                            %
%      Page displaying GPS week number vs. day/date:                           %
%         http://www.ngs.noaa.gov/CORS/gpscal2015.txt                          %
%      Leap second archive:                                                    %
%         http://maia.usno.navy.mil/ser7/tai-utc.dat                           %
%                                                                              %
%------------------------------------------------------------------------------%

yy = utc(1);
mm = utc(2);
dd = utc(3);
HH = utc(4);
MM = utc(5);
SS = utc(6);

days_per_month = [31 28 31 30 31 30 31 31 30 31 30 31];

% input checking
if yy < 1980 || yy > 9999 || mm < 1 || mm > 12 || ...
        dd < 1 || dd > days_per_month(mm) || HH < 0 || HH > 23 || ...
        MM < 0 || MM > 59 || SS < 0 || SS > 59.999999999
    error('Input out of range!!');
elseif leapsec < 0 || leapsec > 99
    error('Leap seconds out of range [0-99]!!');
end

% count days since beginning of GPS time
% initialize 'daycount' to -5 because GPS time started on Jan 06, 1980
daycount = -5;

% accumulate days for each elapsed year since 1980
for ii = 1980:yy-1,
    daycount = daycount + 365;
    % add 1 day for leap years
    if mod(ii,4) == 0
        daycount = daycount + 1;
        % but not when year is divisible by 100
        if mod(ii,100) == 0
            % and year is not divisible by 400
            if mod(ii,400) ~= 0
                daycount = daycount - 1;
            end
        end
    end
end

% count days since Jan 1st of this year
doy = 1;    % this is the days of year counter
for ii = 1:mm-1,
    doy = doy + days_per_month(ii);
    daycount = daycount + days_per_month(ii);
end

% add 1 day for leap years if month is past Feb
if (mm > 2)
    if mod(yy,4) == 0
        doy = doy + 1;
        daycount = daycount + 1;
        % but not when year is divisible by 100
        if mod(yy,100) == 0
            % and year is not divisible by 400
            if mod(yy,400) ~= 0
                doy = doy - 1;
                daycount = daycount - 1;
            end
        end
    end
end

% count days since 1st of month
doy = doy + dd - 1;
daycount = daycount + dd - 1;

% now find what day of the week is it (0-6)
% determine GPS week number as well
dow = daycount;
gpsweek = 0;
while (dow >= 7),
    dow = dow - 7;
    gpsweek = gpsweek + 1;
end

% count seconds since start of week
seconds = (((dow * 24 + HH) * 60) + MM) * 60 + SS;

% include leap seconds between UTC and GPS time
tow = seconds + leapsec;

% protect against end-of-week rollover
if tow > 7*24*3600
    warning('End-of-week rollover detected, attempting recovery!!');
    tow = tow - 7*24*3600;
    gpsweek = gpsweek + 1;
    dow = mod(dow + 1, 7);
    doy = doy + 7;
    if doy > 365 && (mod(yy,4)~=0 || (mod(yy,100)==0 && mod(yy,400)~=0))
        % non leap year rollover
        if yy < 9999
            doy = doy - 365;
        else
            error('Input out of range!!');
        end
    elseif doy > 366 && ((mod(yy,4)==0 && mod(yy,100)~=0) || mod(yy,400)==0)
        % leap year rollover
        if yy < 9999
            doy = doy - 366;
        else
            error('Input out of range!!');
        end
    else
        % no end-of-year rollover
    end
else
    % no end-of-week rollover
end

return
%-------------------------------------------------------------------------------