function navFile = obtain_navFile(time, flags)

%% Server details

GDC_server      = 'igs.bkg.bund.de/';
root_folder_v2  = 'EUREF/obs/';
root_folder_v3  = 'EUREF/nrt/';
hour_delay      = 2;

%% Time-related computations

% Timestamp info
[doy, year4, hh]    = nsgpst2doyyear4(time);
hh                  = num2str(floor(hh) - hour_delay);

doy3                = doy;
% Normalization of doy length
while length(doy3) < 3
    doy3 = strcat('0', num2str(doy));
end

% Normalization of hour length
while length(hh) < 2
    hh = strcat('0', num2str(hh));
end

% Converting year to two digits (v2 only)
if year4 < 2000
    year = num2str(year4 - 1900);
else
    year = num2str(year4 - 2000);
end

%% v2 only
navtype = 'n'; %how to modify this? download all directly?
% navtype = 'g';
% navtype = 'd';

%% About RINEX data (v3 only)
rinex_station   = 'BCLN00ESP';
data_source     = '_R_';
timestamp       = strcat(num2str(year4), num2str(doy3), hh, '00');
period          = '_01H_';
format          = '.rnx';
compression     = '.gz';

%% System/s used
if flags.constellations.GPS
    ctype           = 'GN';
end
if flags.constellations.Galileo
    ctype           = 'EN';
end

%% Filename build (v2)
%nav_fn      = strcat(rinex_station, doy3, '0.', year, navtype);
%navFile     = strcat('RINEX/nav/', nav_fn);

%% Filename build (v3)
nav_fn      = strcat(rinex_station, data_source, timestamp, period, ctype, format);
navFile     = strcat('RINEX/nav_v3/', nav_fn);


%% Download URL(v2)
%navURL = strcat('ftp://', GDC_server, root_folder_v2, num2str(year4), '/', doy3, '/', nav_fn, compression);

%% Download URL (v3)
navURL = strcat('ftp://', GDC_server, root_folder_v3, doy3, '/', hh, '/', nav_fn, compression);

%% File download
urlwrite(navURL, strcat(navFile, compression));

%% Decompression
gunzip(strcat(navFile, compression));

%% Does not work; don't know why
% ftpClient                       =   ftp(GDC_server);
% ftpFiles                        =   dir(ftpClient, strcat('/EUREF/obs/', year4, '/', doy, '/'));


end