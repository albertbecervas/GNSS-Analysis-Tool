function acq_info = extract_info(GNSS_info, j)

% Checking all the struct and separing into constellations

%% Initial declarations

acq_info.SV_list.SVlist_GPSL1     	=   [];
acq_info.SV_list.SVlist_GPSL5     	=   [];
acq_info.SV_list.SVlist_SBAS        =   [];
acq_info.SV_list.SVlist_GLONASS     =   [];
acq_info.SV_list.SVlist_QZSS        =   [];
acq_info.SV_list.SVlist_BEIDOU      =   [];
acq_info.SV_list.SVlist_GalileoE1 	=   [];
acq_info.SV_list.SVlist_GalileoE5a 	=   [];
acq_info.SV_list.SVlist_UNK         =   [];
c                                   =   299792458;

%% Flags
acq_info.flags      =   GNSS_info.Params(j);

%% Location
acq_info.refLocation.LLH = [GNSS_info.Location.latitude GNSS_info.Location.longitude GNSS_info.Location.altitude];
%acq_info.refLocation.LLH = [41.4991 2.1155 179.058]; %geodesic reference
%acq_info.refLocation.LLH = [41.500019, 2.112665 240]; % Q5
acq_info.refLocation.XYZ = lla2ecef(acq_info.refLocation.LLH); 

%% Clock info
if GNSS_info.Clock.hasBiasNanos
    acq_info.nsGNSSTime =  (GNSS_info.Clock.timeNanos - (GNSS_info.Clock.biasNanos + GNSS_info.Clock.fullBiasNanos));
else
    acq_info.nsGNSSTime =  (GNSS_info.Clock.timeNanos - (GNSS_info.Clock.fullBiasNanos));
end
[tow, now]      = nsgpst2gpst(acq_info.nsGNSSTime);
acq_info.TOW    = mod(acq_info.nsGNSSTime, 604800e9)/1e9;
acq_info.NOW    = now;

%% Measurements

nGPSL1          =   1;
nGPSL5          =   1;
nSBAS           =   1;
nGLONASS        =   1;
nQZSS           =   1;
nBEIDOU         =   1;
nGalileoE1      =   1;
nGalileoE5a     =   1;
nUNK            =   1;

for i=1:length(GNSS_info.Meas)

    switch GNSS_info.Meas(i).constellationType
        case 1
            if ~GNSS_info.Meas(i).multipathIndicator
                if GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos ~= 1000000000
                    if check_TOWDECODEDstate(GNSS_info.Meas(i).state)
                        if GNSS_info.Meas(i).carrierFrequencyHz > 1400e6
                            acq_info.SV_list.SVlist_GPSL1                       =   [acq_info.SV_list.SVlist_GPSL1 GNSS_info.Meas(i).svid];
                            acq_info.SV.GPS.GPSL1(nGPSL1).svid              	=   GNSS_info.Meas(i).svid;
                            acq_info.SV.GPS.GPSL1(nGPSL1).state                	=   GNSS_info.Meas(i).state;
                            acq_info.SV.GPS.GPSL1(nGPSL1).multipath             =   GNSS_info.Meas(i).multipathIndicator;
                            acq_info.SV.GPS.GPSL1(nGPSL1).carrierFreq         	=   GNSS_info.Meas(i).carrierFrequencyHz;
                            acq_info.SV.GPS.GPSL1(nGPSL1).t_tx                 	=   GNSS_info.Meas(i).receivedSvTimeNanos + GNSS_info.Meas(i).timeOffsetNanos;
                            acq_info.SV.GPS.GPSL1(nGPSL1).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                            acq_info.SV.GPS.GPSL1(nGPSL1).t_rx                	=   mod(acq_info.nsGNSSTime, 604800e9);
                            acq_info.SV.GPS.GPSL1(nGPSL1).pseudorangeRate     	=   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                            acq_info.SV.GPS.GPSL1(nGPSL1).CN0                 	=   GNSS_info.Meas(i).cn0DbHz;
                            acq_info.SV.GPS.GPSL1(nGPSL1).phase                	=   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                            acq_info.SV.GPS.GPSL1(nGPSL1).phaseState          	=   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                            acq_info.SV.GPS.GPSL1(nGPSL1).p                  	=   pseudo_gen(acq_info.SV.GPS.GPSL1(nGPSL1).t_tx, acq_info.SV.GPS.GPSL1(nGPSL1).t_rx, c);
                            nGPSL1                                              =   nGPSL1 + 1;
                        else
                            acq_info.SV_list.SVlist_GPSL5                       =   [acq_info.SV_list.SVlist_GPSL5 GNSS_info.Meas(i).svid];
                            acq_info.SV.GPS.GPSL5(nGPSL5).svid              	=   GNSS_info.Meas(i).svid;
                            acq_info.SV.GPS.GPSL5(nGPSL5).state               	=   GNSS_info.Meas(i).state;
                            acq_info.SV.GPS.GPSL5(nGPSL5).multipath             =   GNSS_info.Meas(i).multipathIndicator;
                            acq_info.SV.GPS.GPSL5(nGPSL5).carrierFreq         	=   GNSS_info.Meas(i).carrierFrequencyHz;
                            acq_info.SV.GPS.GPSL5(nGPSL5).t_tx                 	=   GNSS_info.Meas(i).receivedSvTimeNanos + GNSS_info.Meas(i).timeOffsetNanos;
                            acq_info.SV.GPS.GPSL5(nGPSL5).t_txnsUncertainty    	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                            acq_info.SV.GPS.GPSL5(nGPSL5).t_rx                 	=   mod(acq_info.nsGNSSTime, 604800e9);
                            acq_info.SV.GPS.GPSL5(nGPSL5).pseudorangeRate     	=   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                            acq_info.SV.GPS.GPSL5(nGPSL5).CN0                  	=   GNSS_info.Meas(i).cn0DbHz;
                            acq_info.SV.GPS.GPSL5(nGPSL5).phase               	=   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                            acq_info.SV.GPS.GPSL5(nGPSL5).phaseState          	=   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                            acq_info.SV.GPS.GPSL5(nGPSL5).p                    	=   pseudo_gen(acq_info.SV.GPS.GPSL5(nGPSL5).t_tx, acq_info.SV.GPS.GPSL5(nGPSL5).t_rx, c);
                            nGPSL5                                              =   nGPSL5 + 1;     
                        end
                    end
                end
            end
        case 2
            acq_info.SV_list.SVlist_SBAS                    =   [acq_info.SV_list.SVlist_SBAS GNSS_info.Meas(i).svid];
            acq_info.SV.SBAS(nSBAS).svid                    =   GNSS_info.Meas(i).svid;
            acq_info.SV.SBAS(nSBAS).carrierFreq             =   GNSS_info.Meas(i).carrierFrequencyHz;
            acq_info.SV.SBAS(nSBAS).t_tx                    =   GNSS_info.Meas(i).receivedSvTimeNanos;
            acq_info.SV.SBAS(nSBAS).pseudorangeRate         =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
            acq_info.SV.SBAS(nSBAS).CN0                     =   GNSS_info.Meas(i).cn0DbHz;
            nSBAS                                           =   nSBAS + 1;
        case 3
            acq_info.SV_list.SVlist_GLONASS             	=   [acq_info.SV_list.SVlist_GLONASS GNSS_info.Meas(i).svid];
            acq_info.SV.GLONASS(nGLONASS).svid           	=   GNSS_info.Meas(i).svid;
            acq_info.SV.GLONASS(nGLONASS).carrierFreq     	=   GNSS_info.Meas(i).carrierFrequencyHz;
            acq_info.SV.GLONASS(nGLONASS).t_tx              =   GNSS_info.Meas(i).receivedSvTimeNanos;
            acq_info.SV.GLONASS(nGLONASS).pseudorangeRate   =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
            acq_info.SV.GLONASS(nGLONASS).CN0               =   GNSS_info.Meas(i).cn0DbHz;
            nGLONASS                                        =   nGLONASS + 1;
        case 4
            acq_info.SV_list.SVlist_QZSS                    =   [acq_info.SV_list.SVlist_QZSS GNSS_info.Meas(i).svid];
            acq_info.SV.QZSS(nQZSS).svid                    =   GNSS_info.Meas(i).svid;
            acq_info.SV.QZSS(nQZSS).carrierFreq             =   GNSS_info.Meas(i).carrierFrequencyHz;
            acq_info.SV.QZSS(nQZSS).t_tx                    =   GNSS_info.Meas(i).receivedSvTimeNanos;
            acq_info.SV.QZSS(nQZSS).pseudorangeRate         =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
            acq_info.SV.QZSS(nQZSS).CN0                     =   GNSS_info.Meas(i).cn0DbHz;
            nQZSS                                           =   nQZSS + 1;

        case 5
            acq_info.SV_list.SVlist_BEIDOU                  =   [acq_info.SV_list.SVlist_BEIDOU GNSS_info.Meas(i).svid];
            acq_info.SV.BEIDOU(nBEIDOU).svid                =   GNSS_info.Meas(i).svid;
            acq_info.SV.BEIDOU(nBEIDOU).carrierFreq      	=   GNSS_info.Meas(i).carrierFrequencyHz;
            acq_info.SV.BEIDOU(nBEIDOU).t_tx                =   GNSS_info.Meas(i).receivedSvTimeNanos;
            acq_info.SV.BEIDOU(nBEIDOU).pseudorangeRate     =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
            acq_info.SV.BEIDOU(nBEIDOU).CN0                 =   GNSS_info.Meas(i).cn0DbHz;
            nBEIDOU                                         =   nBEIDOU + 1;
        case 6
            if ~GNSS_info.Meas(i).multipathIndicator
                if GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos ~= 1000000000
                    if check_TOWDECODEDstate(GNSS_info.Meas(i).state)
                        if GNSS_info.Meas(i).carrierFrequencyHz > 1400e6
                            acq_info.SV_list.SVlist_GalileoE1                               =   [acq_info.SV_list.SVlist_GalileoE1 GNSS_info.Meas(i).svid];
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).svid                  =   GNSS_info.Meas(i).svid;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).state                 =   GNSS_info.Meas(i).state;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).multipath             =   GNSS_info.Meas(i).multipathIndicator;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).carrierFreq           =   GNSS_info.Meas(i).carrierFrequencyHz;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_tx                  =   GNSS_info.Meas(i).receivedSvTimeNanos + GNSS_info.Meas(i).timeOffsetNanos;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_rx              	=   mod(acq_info.nsGNSSTime, 604800e9);
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).pseudorangeRate       =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).CN0                   =   GNSS_info.Meas(i).cn0DbHz;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).phase                 =   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).phaseState            =   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                            acq_info.SV.Galileo.GalileoE1(nGalileoE1).p                     =   pseudo_gen(acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_tx, acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_rx, c);
                            nGalileoE1                                                      =   nGalileoE1 + 1;
                        else
                            acq_info.SV_list.SVlist_GalileoE5a                              =   [acq_info.SV_list.SVlist_GalileoE5a GNSS_info.Meas(i).svid];
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).svid                =   GNSS_info.Meas(i).svid;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).state           	=   GNSS_info.Meas(i).state;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).multipath           =   GNSS_info.Meas(i).multipathIndicator;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).carrierFreq         =   GNSS_info.Meas(i).carrierFrequencyHz;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_tx                =   GNSS_info.Meas(i).receivedSvTimeNanos  + GNSS_info.Meas(i).timeOffsetNanos;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_rx              	=   mod(acq_info.nsGNSSTime, 604800e9);
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).pseudorangeRate     =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).CN0                 =   GNSS_info.Meas(i).cn0DbHz;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).phase               =   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).phaseState          =   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                            acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).p                   =   pseudo_gen(acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_tx, acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_rx, c);
                            nGalileoE5a                                                     =   nGalileoE5a + 1;     
                        end
                    else
                        if check_TOWKNOWNstate(GNSS_info.Meas(i).state)
                            if GNSS_info.Meas(i).carrierFrequencyHz > 1400e6
                                acq_info.SV_list.SVlist_GalileoE1                               =   [acq_info.SV_list.SVlist_GalileoE1 GNSS_info.Meas(i).svid];
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).svid                  =   GNSS_info.Meas(i).svid;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).state                 =   GNSS_info.Meas(i).state;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).multipath             =   GNSS_info.Meas(i).multipathIndicator;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).carrierFreq           =   GNSS_info.Meas(i).carrierFrequencyHz;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_tx                  =   GNSS_info.Meas(i).receivedSvTimeNanos + GNSS_info.Meas(i).timeOffsetNanos;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_rx              	=   mod(acq_info.nsGNSSTime, 604800e9);
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).pseudorangeRate       =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).CN0                   =   GNSS_info.Meas(i).cn0DbHz;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).phase                 =   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).phaseState            =   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                                acq_info.SV.Galileo.GalileoE1(nGalileoE1).p                     =   pseudo_gen(acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_tx, acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_rx, c);
                                nGalileoE1                                                      =   nGalileoE1 + 1;
                            else
                                acq_info.SV_list.SVlist_GalileoE5a                              =   [acq_info.SV_list.SVlist_GalileoE5a GNSS_info.Meas(i).svid];
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).svid                =   GNSS_info.Meas(i).svid;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).state           	=   GNSS_info.Meas(i).state;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).multipath           =   GNSS_info.Meas(i).multipathIndicator;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).carrierFreq         =   GNSS_info.Meas(i).carrierFrequencyHz;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_tx                =   GNSS_info.Meas(i).receivedSvTimeNanos  + GNSS_info.Meas(i).timeOffsetNanos;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_rx              	=   mod(acq_info.nsGNSSTime, 604800e9);
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).pseudorangeRate     =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).CN0                 =   GNSS_info.Meas(i).cn0DbHz;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).phase               =   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).phaseState          =   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                                acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).p                   =   pseudo_gen(acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_tx, acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_rx, c);
                                nGalileoE5a                                                     =   nGalileoE5a + 1;     
                            end
                        else
                            if check_Galstate(GNSS_info.Meas(i).state)
                                if GNSS_info.Meas(i).carrierFrequencyHz > 1400e6
                                    acq_info.SV_list.SVlist_GalileoE1                               =   [acq_info.SV_list.SVlist_GalileoE1 GNSS_info.Meas(i).svid];
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).svid                  =   GNSS_info.Meas(i).svid;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).state                 =   GNSS_info.Meas(i).state;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).multipath             =   GNSS_info.Meas(i).multipathIndicator;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).carrierFreq           =   GNSS_info.Meas(i).carrierFrequencyHz;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_tx                  =   (GNSS_info.Meas(i).receivedSvTimeNanos + GNSS_info.Meas(i).timeOffsetNanos);
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_rx                  =   acq_info.nsGNSSTime;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).pseudorangeRate       =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).CN0                   =   GNSS_info.Meas(i).cn0DbHz;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).phase                 =   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).phaseState            =   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                                    acq_info.SV.Galileo.GalileoE1(nGalileoE1).p                     =   pseudo_gen(mod(acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_tx, 100e6), mod(acq_info.SV.Galileo.GalileoE1(nGalileoE1).t_rx, 100e6), c);
                                    nGalileoE1                                                      =   nGalileoE1 + 1;
                                else
                                    acq_info.SV_list.SVlist_GalileoE5a                              =   [acq_info.SV_list.SVlist_GalileoE5a GNSS_info.Meas(i).svid];
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).svid                =   GNSS_info.Meas(i).svid;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).state           	=   GNSS_info.Meas(i).state;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).multipath           =   GNSS_info.Meas(i).multipathIndicator;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).carrierFreq         =   GNSS_info.Meas(i).carrierFrequencyHz;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_tx                =   (GNSS_info.Meas(i).receivedSvTimeNanos + GNSS_info.Meas(i).timeOffsetNanos);
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_txnsUncertainty  	=   GNSS_info.Meas(i).receivedSvTimeUncertaintyNanos;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_rx                =   acq_info.nsGNSSTime;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).pseudorangeRate     =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).CN0                 =   GNSS_info.Meas(i).cn0DbHz;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).phase               =   GNSS_info.Meas(i).accumulatedDeltaRangeMeters;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).phaseState          =   GNSS_info.Meas(i).accumulatedDeltaRangeState;
                                    acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).p                  	=   pseudo_gen(mod(acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_tx, 100e6), mod(acq_info.SV.Galileo.GalileoE5a(nGalileoE5a).t_rx, 100e6), c);
                                    nGalileoE5a                                                     =   nGalileoE5a + 1;     
                                end
                            end
                        end  
                    end
                end
            end
        otherwise
            acq_info.SV_list.SVlist_UNK                     =   [acq_info.SV_list.SVlist_UNK GNSS_info.Meas(i).svid];
            acq_info.SV.UNK(nUNK).svid                      =   GNSS_info.Meas(i).svid;
            acq_info.SV.UNK(nUNK).carrierFreq               =   GNSS_info.Meas(i).carrierFrequencyHz;
            acq_info.SV.UNK(nUNK).t_tx                      =   GNSS_info.Meas(i).receivedSvTimeNanos;
            acq_info.SV.UNK(nUNK).pseudorangeRate           =   GNSS_info.Meas(i).pseudorangeRateMetersPerSecond;
            acq_info.SV.UNK(nUNK).CN0                       =   GNSS_info.Meas(i).cn0DbHz;
            nUNK                                            =   nUNK + 1;
    end
end

% Sat number correction
nGPSL1    	=   nGPSL1 - 1;
nGPSL5    	=   nGPSL5 - 1;
nSBAS       =   nSBAS - 1;
nGLONASS    =   nGLONASS - 1;
nQZSS       =   nQZSS - 1;
nBEIDOU     =   nBEIDOU - 1;
nGalileoE1  =   nGalileoE1 - 1;
nGalileoE5a =   nGalileoE5a - 1;
nUNK        =   nUNK - 1;

%% Status

for i=1:length(GNSS_info.Status)

    switch GNSS_info.Status(i).constellationType
        case 1
            for j=1:length(acq_info.SV_list.SVlist_GPSL1)
                if acq_info.SV.GPS.GPSL1(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.GPS.GPSL1(j).Azimuth              = GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.GPS.GPSL1(j).Elevation            = GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.GPS.GPSL1(j).OK                   = GNSS_info.Status(i).hasEphemerisData;
                end
            end
            for j=1:length(acq_info.SV_list.SVlist_GPSL5)
                if acq_info.SV.GPS.GPSL5(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.GPS.GPSL5(j).Azimuth              = GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.GPS.GPSL5(j).Elevation            = GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.GPS.GPSL5(j).OK                   = GNSS_info.Status(i).hasEphemerisData;
                end
            end
        case 2
            for j=1:length(acq_info.SV_list.SVlist_SBAS)
                if acq_info.SV.SBAS(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.SBAS(j).Azimuth             = GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.SBAS(j).Elevation       	= GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.SBAS(j).OK                	= GNSS_info.Status(i).hasEphemerisData;
                end
            end
        case 3
            for j=1:length(acq_info.SV_list.SVlist_GLONASS)
                if acq_info.SV.GLONASS(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.GLONASS(j).Azimuth          = GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.GLONASS(j).Elevation        = GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.GLONASS(j).OK               = GNSS_info.Status(i).hasEphemerisData;
                end
            end
        case 4
            for j=1:length(acq_info.SV_list.SVlist_QZSS)
                if acq_info.SV.QZSS(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.QZSS(j).Azimuth         	= GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.QZSS(j).Elevation        	= GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.QZSS(j).OK               	= GNSS_info.Status(i).hasEphemerisData;
                end
            end
        case 5
            for j=1:length(acq_info.SV_list.SVlist_BEIDOU)
                if acq_info.SV.BEIDOU(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.BEIDOU(j).Azimuth           = GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.BEIDOU(j).Elevation         = GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.BEIDOU(j).OK                = GNSS_info.Status(i).hasEphemerisData;
                end
            end
        case 6
%             for j=1:length(acq_info.SV_list.SVlist_GalileoE1)
%                 if acq_info.SV.Galileo.GalileoE1(j).svid == GNSS_info.Meas(i).svid
%                     acq_info.SV.Galileo.GalileoE1(j).Azimuth          = GNSS_info.Status(i).azimuthDegrees;
%                     acq_info.SV.Galileo.GalileoE1(j).Elevation        = GNSS_info.Status(i).elevationDegrees;
%                     acq_info.SV.Galileo.GalileoE1(j).OK               = GNSS_info.Status(i).hasEphemerisData;
%                 end
%             end
%             for j=1:length(acq_info.SV_list.SVlist_GalileoE5a)
%                 if acq_info.SV.Galileo.GalileoE5a(j).svid == GNSS_info.Meas(i).svid
%                     acq_info.SV.Galileo.GalileoE5a(j).Azimuth          = GNSS_info.Status(i).azimuthDegrees;
%                     acq_info.SV.Galileo.GalileoE5a(j).Elevation        = GNSS_info.Status(i).elevationDegrees;
%                     acq_info.SV.Galileo.GalileoE5a(j).OK               = GNSS_info.Status(i).hasEphemerisData;
%                 end
%             end
            if GNSS_info.Status(i).carrierFrequencyHz > 1400e6
                for j=1:length(acq_info.SV.Galileo.GalileoE1)
                    if acq_info.SV.Galileo.GalileoE1(j).svid == GNSS_info.Status(i).svid
                        acq_info.SV.Galileo.GalileoE1(j).Azimuth          = GNSS_info.Status(i).azimuthDegrees;
                        acq_info.SV.Galileo.GalileoE1(j).Elevation        = GNSS_info.Status(i).elevationDegrees;
                        acq_info.SV.Galileo.GalileoE1(j).OK               = GNSS_info.Status(i).hasEphemerisData;
                    end
                end
            else
                for j=1:length(acq_info.SV.Galileo.GalileoE5a)
                    if acq_info.SV.Galileo.GalileoE5a(j).svid == GNSS_info.Status(i).svid
                        acq_info.SV.Galileo.GalileoE5a(j).Azimuth          = GNSS_info.Status(i).azimuthDegrees;
                        acq_info.SV.Galileo.GalileoE5a(j).Elevation        = GNSS_info.Status(i).elevationDegrees;
                        acq_info.SV.Galileo.GalileoE5a(j).OK               = GNSS_info.Status(i).hasEphemerisData;
                    end
                end
            end
            
            
        otherwise
            for j=1:length(acq_info.SV_list.SVlist_UNK)
                if acq_info.SV.UNK(j).svid == GNSS_info.Meas(i).svid
                    acq_info.SV.UNK(j).Azimuth          	= GNSS_info.Status(i).azimuthDegrees;
                    acq_info.SV.UNK(j).Elevation         	= GNSS_info.Status(i).elevationDegrees;
                    acq_info.SV.UNK(j).OK                 	= GNSS_info.Status(i).hasEphemerisData;
                end
            end
    end

end

%% SUPL information

% GPS

for i=1:length(GNSS_info.ephData.GPS)
    % L1
    for j=1:nGPSL1
        if acq_info.SV.GPS.GPSL1(j).svid == GNSS_info.ephData.GPS(i).svid
            
            acq_info.SV.GPS.GPSL1(j).TOW                          =   GNSS_info.ephData.GPS(i).tocS;
            acq_info.SV.GPS.GPSL1(j).NOW                          =   GNSS_info.ephData.GPS(i).week;
            acq_info.SV.GPS.GPSL1(j).af0                          =   GNSS_info.ephData.GPS(i).af0S;
            acq_info.SV.GPS.GPSL1(j).af1                          =   GNSS_info.ephData.GPS(i).af1SecPerSec;
            acq_info.SV.GPS.GPSL1(j).af2                          =   GNSS_info.ephData.GPS(i).af2SecPerSec2;
            acq_info.SV.GPS.GPSL1(j).tgdS                         =   GNSS_info.ephData.GPS(i).tgdS;
            
            % Kepler Model
            acq_info.SV.GPS.GPSL1(j).keplerModel.cic              =   GNSS_info.ephData.GPS(i).keplerModel.cic;
            acq_info.SV.GPS.GPSL1(j).keplerModel.cis              =   GNSS_info.ephData.GPS(i).keplerModel.cis;
            acq_info.SV.GPS.GPSL1(j).keplerModel.crc              =   GNSS_info.ephData.GPS(i).keplerModel.crc;
            acq_info.SV.GPS.GPSL1(j).keplerModel.crs              =   GNSS_info.ephData.GPS(i).keplerModel.crs;
            acq_info.SV.GPS.GPSL1(j).keplerModel.cuc              =   GNSS_info.ephData.GPS(i).keplerModel.cuc;
            acq_info.SV.GPS.GPSL1(j).keplerModel.cus              =   GNSS_info.ephData.GPS(i).keplerModel.cus;
            acq_info.SV.GPS.GPSL1(j).keplerModel.deltaN           =   GNSS_info.ephData.GPS(i).keplerModel.deltaN;
            acq_info.SV.GPS.GPSL1(j).keplerModel.eccentricity     =   GNSS_info.ephData.GPS(i).keplerModel.eccentricity;
            acq_info.SV.GPS.GPSL1(j).keplerModel.i0               =   GNSS_info.ephData.GPS(i).keplerModel.i0;
            acq_info.SV.GPS.GPSL1(j).keplerModel.iDot             =   GNSS_info.ephData.GPS(i).keplerModel.iDot;
            acq_info.SV.GPS.GPSL1(j).keplerModel.m0               =   GNSS_info.ephData.GPS(i).keplerModel.m0;
            acq_info.SV.GPS.GPSL1(j).keplerModel.omega            =   GNSS_info.ephData.GPS(i).keplerModel.omega;
            acq_info.SV.GPS.GPSL1(j).keplerModel.omega0           =   GNSS_info.ephData.GPS(i).keplerModel.omega0;
            acq_info.SV.GPS.GPSL1(j).keplerModel.omegaDot         =   GNSS_info.ephData.GPS(i).keplerModel.omegaDot;
            acq_info.SV.GPS.GPSL1(j).keplerModel.sqrtA            =   GNSS_info.ephData.GPS(i).keplerModel.sqrtA;
            acq_info.SV.GPS.GPSL1(j).keplerModel.toeS             =   GNSS_info.ephData.GPS(i).keplerModel.toeS;
            
        end
    end
    
    % L5
    for j=1:nGPSL5
        if acq_info.SV.GPS.GPSL5(j).svid == GNSS_info.ephData.GPS(i).svid
            
            acq_info.SV.GPS.GPSL5(j).TOW                          =   GNSS_info.ephData.GPS(i).tocS;
            acq_info.SV.GPS.GPSL5(j).NOW                          =   GNSS_info.ephData.GPS(i).week;
            acq_info.SV.GPS.GPSL5(j).af0                          =   GNSS_info.ephData.GPS(i).af0S;
            acq_info.SV.GPS.GPSL5(j).af1                          =   GNSS_info.ephData.GPS(i).af1SecPerSec;
            acq_info.SV.GPS.GPSL5(j).af2                          =   GNSS_info.ephData.GPS(i).af2SecPerSec2;
            acq_info.SV.GPS.GPSL5(j).tgdS                         =   GNSS_info.ephData.GPS(i).tgdS;
            
            % Kepler Model
            acq_info.SV.GPS.GPSL5(j).keplerModel.cic              =   GNSS_info.ephData.GPS(i).keplerModel.cic;
            acq_info.SV.GPS.GPSL5(j).keplerModel.cis              =   GNSS_info.ephData.GPS(i).keplerModel.cis;
            acq_info.SV.GPS.GPSL5(j).keplerModel.crc              =   GNSS_info.ephData.GPS(i).keplerModel.crc;
            acq_info.SV.GPS.GPSL5(j).keplerModel.crs              =   GNSS_info.ephData.GPS(i).keplerModel.crs;
            acq_info.SV.GPS.GPSL5(j).keplerModel.cuc              =   GNSS_info.ephData.GPS(i).keplerModel.cuc;
            acq_info.SV.GPS.GPSL5(j).keplerModel.cus              =   GNSS_info.ephData.GPS(i).keplerModel.cus;
            acq_info.SV.GPS.GPSL5(j).keplerModel.deltaN           =   GNSS_info.ephData.GPS(i).keplerModel.deltaN;
            acq_info.SV.GPS.GPSL5(j).keplerModel.eccentricity     =   GNSS_info.ephData.GPS(i).keplerModel.eccentricity;
            acq_info.SV.GPS.GPSL5(j).keplerModel.i0               =   GNSS_info.ephData.GPS(i).keplerModel.i0;
            acq_info.SV.GPS.GPSL5(j).keplerModel.iDot             =   GNSS_info.ephData.GPS(i).keplerModel.iDot;
            acq_info.SV.GPS.GPSL5(j).keplerModel.m0               =   GNSS_info.ephData.GPS(i).keplerModel.m0;
            acq_info.SV.GPS.GPSL5(j).keplerModel.omega            =   GNSS_info.ephData.GPS(i).keplerModel.omega;
            acq_info.SV.GPS.GPSL5(j).keplerModel.omega0           =   GNSS_info.ephData.GPS(i).keplerModel.omega0;
            acq_info.SV.GPS.GPSL5(j).keplerModel.omegaDot         =   GNSS_info.ephData.GPS(i).keplerModel.omegaDot;
            acq_info.SV.GPS.GPSL5(j).keplerModel.sqrtA            =   GNSS_info.ephData.GPS(i).keplerModel.sqrtA;
            acq_info.SV.GPS.GPSL5(j).keplerModel.toeS             =   GNSS_info.ephData.GPS(i).keplerModel.toeS;
            
        end
    end
end

% Galileo
for i=1:length(GNSS_info.ephData.Galileo)
    % E1
    for j=1:nGalileoE1
        if acq_info.SV.Galileo.GalileoE1(j).svid == GNSS_info.ephData.Galileo(i).svid
            if GNSS_info.ephData.Galileo(i).isINav == true
                acq_info.SV.Galileo.GalileoE1(j).isINav                         =   GNSS_info.ephData.Galileo(i).isINav;
                acq_info.SV.Galileo.GalileoE1(j).TOW                          =   GNSS_info.ephData.Galileo(i).tocS;
                acq_info.SV.Galileo.GalileoE1(j).NOW                          =   GNSS_info.ephData.Galileo(i).week;
                acq_info.SV.Galileo.GalileoE1(j).af0                          =   GNSS_info.ephData.Galileo(i).af0S;
                acq_info.SV.Galileo.GalileoE1(j).af1                          =   GNSS_info.ephData.Galileo(i).af1SecPerSec;
                acq_info.SV.Galileo.GalileoE1(j).af2                          =   GNSS_info.ephData.Galileo(i).af2SecPerSec2;
                acq_info.SV.Galileo.GalileoE1(j).tgdS                         =   GNSS_info.ephData.Galileo(i).tgdS;

                % Kepler Model
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.cic              =   GNSS_info.ephData.Galileo(i).keplerModel.cic;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.cis              =   GNSS_info.ephData.Galileo(i).keplerModel.cis;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.crc              =   GNSS_info.ephData.Galileo(i).keplerModel.crc;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.crs              =   GNSS_info.ephData.Galileo(i).keplerModel.crs;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.cuc              =   GNSS_info.ephData.Galileo(i).keplerModel.cuc;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.cus              =   GNSS_info.ephData.Galileo(i).keplerModel.cus;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.deltaN           =   GNSS_info.ephData.Galileo(i).keplerModel.deltaN;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.eccentricity     =   GNSS_info.ephData.Galileo(i).keplerModel.eccentricity;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.i0               =   GNSS_info.ephData.Galileo(i).keplerModel.i0;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.iDot             =   GNSS_info.ephData.Galileo(i).keplerModel.iDot;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.m0               =   GNSS_info.ephData.Galileo(i).keplerModel.m0;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.omega            =   GNSS_info.ephData.Galileo(i).keplerModel.omega;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.omega0           =   GNSS_info.ephData.Galileo(i).keplerModel.omega0;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.omegaDot         =   GNSS_info.ephData.Galileo(i).keplerModel.omegaDot;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.sqrtA            =   GNSS_info.ephData.Galileo(i).keplerModel.sqrtA;
                acq_info.SV.Galileo.GalileoE1(j).keplerModel.toeS             =   GNSS_info.ephData.Galileo(i).keplerModel.toeS;
            end
        end
    end
    
    % E5a
    for j=1:nGalileoE5a
        if acq_info.SV.Galileo.GalileoE5a(j).svid == GNSS_info.ephData.Galileo(i).svid
            if GNSS_info.ephData.Galileo(i).isINav == false
                acq_info.SV.Galileo.GalileoE5a(j).isINav                       =   GNSS_info.ephData.Galileo(i).isINav;
                acq_info.SV.Galileo.GalileoE5a(j).TOW                          =   GNSS_info.ephData.Galileo(i).tocS;
                acq_info.SV.Galileo.GalileoE5a(j).NOW                          =   GNSS_info.ephData.Galileo(i).week;
                acq_info.SV.Galileo.GalileoE5a(j).af0                          =   GNSS_info.ephData.Galileo(i).af0S;
                acq_info.SV.Galileo.GalileoE5a(j).af1                          =   GNSS_info.ephData.Galileo(i).af1SecPerSec;
                acq_info.SV.Galileo.GalileoE5a(j).af2                          =   GNSS_info.ephData.Galileo(i).af2SecPerSec2;
                acq_info.SV.Galileo.GalileoE5a(j).tgdS                         =   GNSS_info.ephData.Galileo(i).tgdS;

                % Kepler Model
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.cic              =   GNSS_info.ephData.Galileo(i).keplerModel.cic;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.cis              =   GNSS_info.ephData.Galileo(i).keplerModel.cis;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.crc              =   GNSS_info.ephData.Galileo(i).keplerModel.crc;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.crs              =   GNSS_info.ephData.Galileo(i).keplerModel.crs;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.cuc              =   GNSS_info.ephData.Galileo(i).keplerModel.cuc;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.cus              =   GNSS_info.ephData.Galileo(i).keplerModel.cus;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.deltaN           =   GNSS_info.ephData.Galileo(i).keplerModel.deltaN;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.eccentricity     =   GNSS_info.ephData.Galileo(i).keplerModel.eccentricity;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.i0               =   GNSS_info.ephData.Galileo(i).keplerModel.i0;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.iDot             =   GNSS_info.ephData.Galileo(i).keplerModel.iDot;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.m0               =   GNSS_info.ephData.Galileo(i).keplerModel.m0;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.omega            =   GNSS_info.ephData.Galileo(i).keplerModel.omega;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.omega0           =   GNSS_info.ephData.Galileo(i).keplerModel.omega0;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.omegaDot         =   GNSS_info.ephData.Galileo(i).keplerModel.omegaDot;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.sqrtA            =   GNSS_info.ephData.Galileo(i).keplerModel.sqrtA;
                acq_info.SV.Galileo.GalileoE5a(j).keplerModel.toeS             =   GNSS_info.ephData.Galileo(i).keplerModel.toeS;
            end
        end
    end
end


if nGPSL1 ~= 0
        count = 1;
        noEph = [];
        for i=1:length(acq_info.SV.GPS.GPSL1)
           if isempty(acq_info.SV.GPS.GPSL1(i).TOW)
               noEph(count) = i;
               count = count + 1;
           end
        end

    noEph = fliplr(noEph);
    for i=1:length(noEph)
        acq_info.SV.GPS.GPSL1(noEph(i)) = [];
    end
end

if nGPSL5 ~= 0
        count = 1;
        noEph = [];
        for i=1:length(acq_info.SV.GPS.GPSL5)
           if isempty(acq_info.SV.GPS.GPSL5(i).TOW)
               noEph(count) = i;
               count = count + 1;
           end
        end

    noEph = fliplr(noEph);
    for i=1:length(noEph)
        acq_info.SV.GPS.GPSL5(noEph(i)) = [];
    end
end

if nGalileoE1 ~= 0
    count = 1;
    noEph = [];
    for i=1:length(acq_info.SV.Galileo.GalileoE1)
       if isempty(acq_info.SV.Galileo.GalileoE1(i).TOW)
           noEph(count) = i;
           count = count + 1;
       end
    end

    noEph = fliplr(noEph);
    for i=1:length(noEph)
        acq_info.SV.Galileo.GalileoE1(noEph(i)) = [];
    end
end

if nGalileoE5a ~= 0
    count = 1;
    noEph = [];
    for i=1:length(acq_info.SV.Galileo.GalileoE5a)
       if isempty(acq_info.SV.Galileo.GalileoE5a(i).TOW)
           noEph(count) = i;
           count = count + 1;
       end
    end

    noEph = fliplr(noEph);
    for i=1:length(noEph)
        acq_info.SV.Galileo.GalileoE5a(noEph(i)) = [];
    end
end

% ionoProto
acq_info.ionoProto                        =   [GNSS_info.ephData.Klobuchar.alpha_; GNSS_info.ephData.Klobuchar.beta_];
%acq_info.ionoProto                        =   [GNSS_info.ephData.ionoProto.alpha_; GNSS_info.ephData.ionoProto.beta_];

%% Number of total SV
acq_info.SVs = nGPSL1 + nGPSL5 + nSBAS + nGLONASS + nQZSS + nBEIDOU + nGalileoE1 + nGalileoE5a + nUNK;
end