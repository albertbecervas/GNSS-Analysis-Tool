function eph    =   getEphMatrix(SVinfo, flags)

    %% GPS
    if flags.constellations.GPS
        GPS         =   SVinfo.gpsSatellites.gpsL1;
        ephemerides     =   zeros(22, length(GPS));

        for i=1:length(GPS)
            ephemerides(1, i)       =   GPS(i).svid;
            ephemerides(2, i)       =   GPS(i).af2;
            ephemerides(3, i)       =   GPS(i).keplerModel.m0;
            ephemerides(4, i)       =   GPS(i).keplerModel.sqrtA;
            ephemerides(5, i)       =   GPS(i).keplerModel.deltaN;
            ephemerides(6, i)       =   GPS(i).keplerModel.eccentricity;
            ephemerides(7, i)       =   GPS(i).keplerModel.omega;
            ephemerides(8, i)       =   GPS(i).keplerModel.cuc;
            ephemerides(9, i)       =   GPS(i).keplerModel.cus;
            ephemerides(10, i)      =   GPS(i).keplerModel.crc;
            ephemerides(11, i)      =   GPS(i).keplerModel.crs;
            ephemerides(12, i)      =   GPS(i).keplerModel.i0;
            ephemerides(13, i)      =   GPS(i).keplerModel.iDot;
            ephemerides(14, i)      =   GPS(i).keplerModel.cic;
            ephemerides(15, i)      =   GPS(i).keplerModel.cis;
            ephemerides(16, i)      =   GPS(i).keplerModel.omega0;
            ephemerides(17, i)      =   GPS(i).keplerModel.omegaDot;
            ephemerides(18, i)      =   GPS(i).keplerModel.toeS;
            ephemerides(19, i)      =   GPS(i).af0;
            ephemerides(20, i)      =   GPS(i).af1;
            ephemerides(21, i)      =   GPS(i).keplerModel.toeS;
            ephemerides(22, i)      =   GPS(i).tgdS;
        end
        eph.gpsL1 = ephemerides;

        GPS         =   SVinfo.gpsSatellites.gpsL5;
        ephemerides     =   zeros(22, length(GPS));

        for i=1:length(GPS)
            ephemerides(1, i)       =   GPS(i).svid;
            ephemerides(2, i)       =   GPS(i).af2;
            ephemerides(3, i)       =   GPS(i).keplerModel.m0;
            ephemerides(4, i)       =   GPS(i).keplerModel.sqrtA;
            ephemerides(5, i)       =   GPS(i).keplerModel.deltaN;
            ephemerides(6, i)       =   GPS(i).keplerModel.eccentricity;
            ephemerides(7, i)       =   GPS(i).keplerModel.omega;
            ephemerides(8, i)       =   GPS(i).keplerModel.cuc;
            ephemerides(9, i)       =   GPS(i).keplerModel.cus;
            ephemerides(10, i)      =   GPS(i).keplerModel.crc;
            ephemerides(11, i)      =   GPS(i).keplerModel.crs;
            ephemerides(12, i)      =   GPS(i).keplerModel.i0;
            ephemerides(13, i)      =   GPS(i).keplerModel.iDot;
            ephemerides(14, i)      =   GPS(i).keplerModel.cic;
            ephemerides(15, i)      =   GPS(i).keplerModel.cis;
            ephemerides(16, i)      =   GPS(i).keplerModel.omega0;
            ephemerides(17, i)      =   GPS(i).keplerModel.omegaDot;
            ephemerides(18, i)      =   GPS(i).keplerModel.toeS;
            ephemerides(19, i)      =   GPS(i).af0;
            ephemerides(20, i)      =   GPS(i).af1;
            ephemerides(21, i)      =   GPS(i).keplerModel.toeS;
            ephemerides(22, i)      =   GPS(i).tgdS;
        end
        eph.gpsL5 = ephemerides;
    end



    %% Galileo (ToDo !!!!)
    if flags.constellations.Galileo
        Galileo         =   SVinfo.galSatellites.galE1;
        ephemerides     =   zeros(22, length(Galileo));

        for i=1:length(Galileo)
            ephemerides(1, i)       =   Galileo(i).svid;
            ephemerides(2, i)       =   Galileo(i).af2;
            ephemerides(3, i)       =   Galileo(i).keplerModel.m0;
            ephemerides(4, i)       =   Galileo(i).keplerModel.sqrtA;
            ephemerides(5, i)       =   Galileo(i).keplerModel.deltaN;
            ephemerides(6, i)       =   Galileo(i).keplerModel.eccentricity;
            ephemerides(7, i)       =   Galileo(i).keplerModel.omega;
            ephemerides(8, i)       =   Galileo(i).keplerModel.cuc;
            ephemerides(9, i)       =   Galileo(i).keplerModel.cus;
            ephemerides(10, i)      =   Galileo(i).keplerModel.crc;
            ephemerides(11, i)      =   Galileo(i).keplerModel.crs;
            ephemerides(12, i)      =   Galileo(i).keplerModel.i0;
            ephemerides(13, i)      =   Galileo(i).keplerModel.iDot;
            ephemerides(14, i)      =   Galileo(i).keplerModel.cic;
            ephemerides(15, i)      =   Galileo(i).keplerModel.cis;
            ephemerides(16, i)      =   Galileo(i).keplerModel.omega0;
            ephemerides(17, i)      =   Galileo(i).keplerModel.omegaDot;
            ephemerides(18, i)      =   Galileo(i).keplerModel.toeS;
            ephemerides(19, i)      =   Galileo(i).af0;
            ephemerides(20, i)      =   Galileo(i).af1;
            ephemerides(21, i)      =   Galileo(i).keplerModel.toeS;
            ephemerides(22, i)      =   Galileo(i).tgdS;
        end
        eph.galE1 = ephemerides;

        Galileo         =   SVinfo.galSatellites.galE5a;
        ephemerides     =   zeros(22, length(Galileo));

        for i=1:length(Galileo)
            ephemerides(1, i)       =   Galileo(i).svid;
            ephemerides(2, i)       =   Galileo(i).af2;
            ephemerides(3, i)       =   Galileo(i).keplerModel.m0;
            ephemerides(4, i)       =   Galileo(i).keplerModel.sqrtA;
            ephemerides(5, i)       =   Galileo(i).keplerModel.deltaN;
            ephemerides(6, i)       =   Galileo(i).keplerModel.eccentricity;
            ephemerides(7, i)       =   Galileo(i).keplerModel.omega;
            ephemerides(8, i)       =   Galileo(i).keplerModel.cuc;
            ephemerides(9, i)       =   Galileo(i).keplerModel.cus;
            ephemerides(10, i)      =   Galileo(i).keplerModel.crc;
            ephemerides(11, i)      =   Galileo(i).keplerModel.crs;
            ephemerides(12, i)      =   Galileo(i).keplerModel.i0;
            ephemerides(13, i)      =   Galileo(i).keplerModel.iDot;
            ephemerides(14, i)      =   Galileo(i).keplerModel.cic;
            ephemerides(15, i)      =   Galileo(i).keplerModel.cis;
            ephemerides(16, i)      =   Galileo(i).keplerModel.omega0;
            ephemerides(17, i)      =   Galileo(i).keplerModel.omegaDot;
            ephemerides(18, i)      =   Galileo(i).keplerModel.toeS;
            ephemerides(19, i)      =   Galileo(i).af0;
            ephemerides(20, i)      =   Galileo(i).af1;
            ephemerides(21, i)      =   Galileo(i).keplerModel.toeS;
            ephemerides(22, i)      =   Galileo(i).tgdS;
        end
        eph.galE5a = ephemerides;
    end
end