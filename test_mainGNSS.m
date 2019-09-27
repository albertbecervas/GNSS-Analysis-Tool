% function test_mainGNSS()

tic

close all
clear all
clc;

%% Choosing data      GPS 1     GAL E5a     GAL E1 
opmin   =   910;%      929        923        910            637;%646;
opmax	=   931;%      931        928        922            797;%698;

latLongs = [];
opCount = 1;

for op=opmin:opmax
%     op
    
    %% Get JSON filename 
    json_fn = getJSONfn(op);
    fprintf('File: %d', opCount);

    %% Build str from JSON
    json            = strcat('JSON/', json_fn);
    json_content    = load_info(json);
    
    %% Execution
    [results, ref] = main_GNSS(json_content);
    
    %% Prints and plots

    for i=1:size(results, 1)
        p_err           =   sqrt((lla2ecef(ref(1:3)) - lla2ecef(results(i, 1:3))).^2);
        D2              =   sqrt((p_err(1))^2 + (p_err(2))^2);
        D3              =   sqrt((p_err(1))^2 + (p_err(2))^2 + (p_err(3))^2);
        GDOP            =   results(i, 5);
        PDOP            =   results(i, 6);
        TDOP            =   results(i, 7);
        GPS_iono        =   results(i, 8);
        GPS_tropo       =   results(i, 9);
        Galileo_iono    =   results(i, 10);
        Galileo_tropo   =   results(i, 11);
        NS              =   results(i, 12);
        
        fprintf('Reference position: \n');
        fprintf('Latitude: %f Longitude: %f Height: %f\n', ref(1), ref(2), ref(3));
        fprintf('\n(Averaged) computed position and time for configuration %G\n', i);
        fprintf('Latitude: %f Longitude: %f Height: %f Time bias: %f\n', results(i, 1), results(i, 2), results(i, 3), results(i, 4));
        fprintf('GDOP: %f PDOP: %f TDOP: %f\n', results(i, 5), results(i, 6), results(i, 7));
        fprintf(strcat('2D error: %f m\n'), D2);
        fprintf(strcat('3D error: %f m\n'), D3);
    end

    latLongs(opCount, 1) = results(i, 1);
    latLongs(opCount, 2) = results(i, 2);
    opCount = opCount + 1;
    
    if isempty(results)
        D2              = NaN; 
        D3              = NaN;
      	GDOP            = NaN;
        TDOP            = NaN;
        PDOP            = NaN;
        GPS_iono        = NaN;
        GPS_tropo       = NaN;
        Galileo_iono    = NaN;
        Galileo_tropo   = NaN;
        NS              = 0;
    end

    TOTALres_D2(op-(opmin-1))               =   D2;
    TOTALres_D3(op-(opmin-1))               =   D3;
    TOTAL_resGDOP(op-(opmin-1))             =   GDOP;
    TOTAL_resPDOP(op-(opmin-1))             =   PDOP;
    TOTAL_resTDOP(op-(opmin-1))             =   TDOP;
    TOTAL_resGPS_iono(op-(opmin-1))         =   GPS_iono;
    TOTAL_resGPS_tropo(op-(opmin-1))        =   GPS_tropo;
    TOTAL_resGalileo_iono(op-(opmin-1))     =   Galileo_iono;
    TOTAL_resGalileo_tropo(op-(opmin-1))    =   Galileo_tropo;
    TOTAL_resNS(op-(opmin-1))               =   NS;
    
end

fprintf('\n\n For Google: \n');
for i=1:opCount-1
    fprintf('%f, %f \n', latLongs(i, 1), latLongs(i, 2));
end

TOTALres_D2mean           =   mean(TOTALres_D2);
TOTALres_D3mean           =   mean(TOTALres_D3);

%% Plots
plot(TOTALres_D2(1:(opmax-(opmin-1))))
title('2D error')
hold on
plot(TOTALres_D2mean*ones(1, length(TOTALres_D2)))

figure
plot(TOTALres_D3(1:(opmax-(opmin-1))))
title('3D error')
hold on
plot(TOTALres_D3mean*ones(1, length(TOTALres_D3)))

figure
ecdf(TOTALres_D2)
title('2D error CDF')

figure
ecdf(TOTALres_D3)
title('3D error CDF')

figure
plot(TOTAL_resGDOP(1:(opmax-(opmin-1))))
title('GDOP')

figure
plot(TOTAL_resPDOP(1:(opmax-(opmin-1))))
title('PDOP')

figure
plot(TOTAL_resTDOP(1:(opmax-(opmin-1))))
title('TDOP')

figure
plot(TOTAL_resGPS_iono(1:(opmax-(opmin-1))))
title('GPS iono correction')

figure
plot(TOTAL_resGPS_tropo(1:(opmax-(opmin-1))))
title('GPS tropo correction')

figure
plot(TOTAL_resGalileo_iono(1:(opmax-(opmin-1))))
title('Galileo iono correction')

figure
plot(TOTAL_resGalileo_tropo(1:(opmax-(opmin-1))))
title('Galileo tropo correction')

figure
plot(TOTAL_resNS(1:(op-(opmin-1))))
title('Satellite number')

TOTALres_D2mean;
TOTALres_D3mean;

toc

% end

% plot(TOTALres((10-9):(33-9))) % geodesic
% title('Geodesic tests')
% figure
% plot(TOTALres((34-9):(81-9))) % Q5
% title('Q5 tests')
% figure
% plot(TOTALres((82-9):(179-9))) % along firef
% title('Around firef tests')
% figure
% plot(TOTALres((180-9):(206-9))) % firef to caf
% title('Firef to caf tests')
% figure
% plot(TOTALres((207-9):(323-9))) % car
% title('Car tests')
% figure
% plot(TOTALres((340-339):(451-339))) % Arnau to UAB
% title('Arnau to UAB tests')
% figure
% plot(TOTALres((452-451):(529-451))) % Alej to Egi
% title('Alej to Egi tests')
% figure

% for i=1:size(results, 1)   
%     fprintf('Configuration %G. Copy to Google:\n', i);
%     fprintf('%f, %f\n\n', results(i, 1), results(i, 2));
% end