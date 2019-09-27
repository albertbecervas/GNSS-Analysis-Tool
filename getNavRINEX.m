function    [eph,iono]  =   getNavRINEX(NavRINEXFile)
% get_eph:  Reads a RINEX Navigation file and reformats the data into a
%           matrix with 22 rows with the ephemerides information and a 
%           column for each satellite/epoch. The code is done for RINEX
%           2.XX, check properties of RINEX 3.XX.
%
% Input:            
%           NavRINEXFile:   File name of the Rinex Navigation file
%
% Output:   eph:            22xNeph matrix with the ephemerides information
%                           for the satellites contained in the RINEX file. 
%                           Units are either seconds, meters, or radians  
%           iono:           8x1 vector containing ionosphere parameters
%
% Modified from Kai Borre 04-18-96
% Copyright (c) by Kai Borre
% $Revision: 1.0 $  $Date: 1997/09/24  $

    fide        =   fopen(NavRINEXFile);
    head_lines  =   0;
    iono        =   zeros(8,1);
    const       =   [];
    %
    %-  Read the header. Skip all the header (look for 'END OF HEADER' 
    %   string) except the ionosphere labels, which are read
    answer      =   [];
    while(isempty(answer))  
       head_lines   =   head_lines+1;   %   Count the number of header lines
       line         =   fgetl(fide);    %   Read next line
       %
       %    Version flag
       vers_found   =   ~isempty(strfind(line,'RINEX VERSION / TYPE'));
       if(vers_found)
           version  =   str2double(line(1:9));   %   Get RINEX version
       end
       %    Iono info flag
       iono_found   =   (~isempty(strfind(line,'ION ALPHA')) ||...
                        ~isempty(strfind(line,'IONOSPHERIC CORR')));
       if(iono_found)   %   If the ionosphere parameters label was found
           %--  Save the 8 ionosphere parameters
           %
           %---     Get first 4 parameters
           if( version >= 3 )
               data     =   textscan(line(5:end),'%f%f%f%f%*[^\n]');
               id       =   1;
           else
               data     =   textscan(line(1:end),'%f%f%f%f%*[^\n]');
               id       =   0;
           end
           const        =   line(1:3);  % Get constellation
           %
            if(strcmp(const,'GPS'))     % Get iono param for GPS/GLO
                iono(1) = data{1};
                iono(2) = data{2};
                iono(3) = data{3};
                iono(4) = data{4};
                line    = [];
                while isempty(line)
                    line = fgetl(fide);
                    head_lines  =   head_lines + 1;
                end
                data = textscan(line(5:end),'%f%f%f%f%*[^\n]');
                if ~isempty(data(4))
                    iono(5) =   data{1};
                    iono(6) =   data{2};
                    iono(7) =   data{3};
                    iono(8) =   data{4};
                else
                    iono    =   zeros(8,1);
                end
            else                        % We do not use iono param for GAL
                iono        =   [];
            end
       end
       answer       =   strfind(line,'END OF HEADER');
    end
    if( isempty(const) )
        const       =   'GLO';
    end
    %
    %-  Get number of ephemerides/satellites (Read the whole file and count the
    %   lines, the move the file pointer to the begining of the file)
    noeph   =   0;
    line    =   fgetl(fide);
    while(line ~= -1)
       noeph = noeph+1;
       line = fgetl(fide);
    %    if line == -1, break;  end
    end
    noeph   =   noeph/8;    % Each ephemerides contains 8 lines
    frewind(fide);          % Come back to the begining of the file
    % Skip the header again
    for ii = 1:head_lines 
        line = fgetl(fide); 
    end
    %
    if( strcmp(const,'GPS') || strcmp(const,'GAL'))
        %-  Set aside memory for the input
        svprn       =   zeros(1,noeph);
    %     weekno      =   zeros(1,noeph);
    %     t0c         =   zeros(1,noeph);
        tgd         =   zeros(1,noeph);
    %     aodc        =   zeros(1,noeph);
    %     toe         =   zeros(1,noeph);
        af2         =   zeros(1,noeph);
        af1         =   zeros(1,noeph);
        af0         =   zeros(1,noeph);
    %     aode        =   zeros(1,noeph);
        deltan      =   zeros(1,noeph);
        M0          =   zeros(1,noeph);
        ecc         =   zeros(1,noeph);
        roota       =   zeros(1,noeph);
        toe         =   zeros(1,noeph);
        cic         =   zeros(1,noeph);
        crc         =   zeros(1,noeph);
        cis         =   zeros(1,noeph);
        crs         =   zeros(1,noeph);
        cuc         =   zeros(1,noeph);
        cus         =   zeros(1,noeph);
        Omega0      =   zeros(1,noeph);
        omega       =   zeros(1,noeph);
        i0          =   zeros(1,noeph);
        Omegadot    =   zeros(1,noeph);
        idot        =   zeros(1,noeph);
    %     accuracy    =   zeros(1,noeph);
    %     health      =   zeros(1,noeph);
    %     fit         =   zeros(1,noeph);
        %
        %- Get the data taking into account the RINEX format (from Kai Borre)
        for ii = 1:noeph
            line         =   fgetl(fide);               %   1st line
            svprn(ii)    =   str2num(line(id+(1:2)));
    %        year         =   line(3:6);
    %        month        =   line(7:9);
    %        day          =   line(10:12);
    %        hour         =   line(13:15);
    %        minute       =   line(16:18);
    %        second       =   line(19:22);
            af0(ii)    = str2num(line(id+(23:41)));
            af1(ii)    = str2num(line(id+(42:60)));
            af2(ii)    = str2num(line(id+(61:79))); 
           %
            line         =   fgetl(fide);               %   2nd line  
    %        IODE         =   line(4:22);
            crs(ii)    = str2num(line(id+(23:41)));
            deltan(ii) = str2num(line(id+(42:60)));
            M0(ii)     = str2num(line(id+(61:79)));
           %
            line         =   fgetl(fide);               %   3rd line
            cuc(ii)    = str2num(line(id+(4:22)));
            ecc(ii)    = str2num(line(id+(23:41)));
            cus(ii)    = str2num(line(id+(42:60)));
            roota(ii)  = str2num(line(id+(61:79)));
           %
           line         =   fgetl(fide);                %   4th line
           toe(ii)      =   str2num(line(id+(4:22)));
            if (strcmp(const,'GAL') && toe(ii) > 2500) 
                toe(ii) = toe(ii) - 1024; 
            end
           cic(ii)      =   str2num(line(id+(23:41)));
           Omega0(ii)   =   str2num(line(id+(42:60)));
           cis(ii)      =   str2num(line(id+(61:79)));
           %
           line         =   fgetl(fide);                %   5th line   
           i0(ii)       =   str2num(line(id+(4:22)));
           crc(ii)      =   str2num(line(id+(23:41)));
           omega(ii)    =   str2num(line(id+(42:60)));
           Omegadot(ii) =   str2num(line(id+(61:79)));
           %
           line         =   fgetl(fide);                %   6th line 
           idot(ii)     =   str2num(line(id+(4:22)));
    %        codes        =   str2num(line(23:41));
    %        weekno       =   str2num(line(id+42:60));
    %        L2flag       =   str2num(line(61:79));
           %
           line         =   fgetl(fide);                %   7th line 
    %        svaccur      =   str2num(line(4:22));
    %        svhealth     =   str2num(line(23:41));
           tgd(ii)      =   str2num(line(id+(42:60)));
    %        iodc         =   line(61:79);
           %
           line         =   fgetl(fide);                %   8th line 
    %        tom(ii)      =   str2num(line(id+4:22));
        end
        %
        %
        %-  Description of variable eph.
        eph(1,:)    =   svprn;
        eph(2,:)    =   af2;
        eph(3,:)    =   M0;
        eph(4,:)    =   roota;
        eph(5,:)    =   deltan;
        eph(6,:)    =   ecc;
        eph(7,:)    =   omega;
        eph(8,:)    =   cuc;
        eph(9,:)    =   cus;
        eph(10,:)   =   crc;
        eph(11,:)   =   crs;
        eph(12,:)   =   i0;
        eph(13,:)   =   idot;
        eph(14,:)   =   cic;
        eph(15,:)   =   cis;
        eph(16,:)   =   Omega0;
        eph(17,:)   =   Omegadot;
        eph(18,:)   =   toe;
        eph(19,:)   =   af0;
        eph(20,:)   =   af1;
        eph(21,:)   =   toe;
        eph(22,:)   =   tgd;
        %
    elseif( strcmp(const,'GLO') )
        i   =   0;
        id = 0;
        while(~feof(fide))
            lin1    =   [];
            lin2    =   [];
            lin3    =   [];
            lin4    =   [];
            lin5    =   [];
            lin6    =   [];
            lin7    =   [];
            lin8    =   [];
            %
            o       =   id;
            i       =   i + 1;

            %read the first line (containing system and time information)
            while isempty(lin1)
                lin1 = fgetl(fide);
            end
            if (lin1 == -1)
                break
            end
            if (~isempty(strfind(lin1,'COMMENT')))
                continue
            end
            %
            %read the next 3 lines (common to all systems)
            while isempty(lin2)
                lin2 = fgetl(fide);
            end
            while isempty(lin3)
                lin3 = fgetl(fide);
            end
            while isempty(lin4)
                lin4 = fgetl(fide);
            end
            %
            svprn  = str2num(lin1(o+[1:2])); %When input is a scalar, str2double is better than str2num. But str2double does not support 'D'
            if (version < 3)
                year   = str2num(lin1(o+[3:6])); %year = four_digit_year(year);
                month  = str2num(lin1(o+[7:9]));
                day    = str2num(lin1(o+[10:12]));
                hour   = str2num(lin1(o+[13:15]));
                minute = str2num(lin1(o+[16:18]));
                second = str2num(lin1(o+[19:22]));
                if year < 1000
                    year = four_digit_year(year);
                end

            else
                year   = str2num(lin1(o+[3:6]+1));
                month  = str2num(lin1(o+[7:9]+1));
                day    = str2num(lin1(o+[10:12]+1));
                hour   = str2num(lin1(o+[13:15]+1));
                minute = str2num(lin1(o+[16:18]+1));
                second = str2num(lin1(o+[19:21]+1));
            end
            %
            TauN   = -str2num(lin1(o+[23:41]));
            GammaN = str2num(lin1(o+[42:60]));
            tk     = str2num(lin1(o+[61:79]));

            X      = str2num(lin2(o+[4:22]));
            Xv     = str2num(lin2(o+[23:41]));
            Xa     = str2num(lin2(o+[42:60]));
            Bn     = str2num(lin2(o+[61:79])); %health flag

            Y      = str2num(lin3(o+[4:22]));
            Yv     = str2num(lin3(o+[23:41]));
            Ya     = str2num(lin3(o+[42:60]));
            freq_num = str2num(lin3(o+[61:79])); %frequency number

            Z      = str2num(lin4(o+[4:22]));
            Zv     = str2num(lin4(o+[23:41]));
            Za     = str2num(lin4(o+[42:60]));
            E      = str2num(lin4(o+[61:79])); %age of oper. information  (days)

            %frequencies on L1 and L2
            %freq_L1 = freq_num * 0.5625 + 1602.0;
            %freq_L2 = freq_num * 0.4375 + 1246.0;

            %convert GLONASS (UTC) date to GPS date
            %date_GLO = datenum([year month day hour minute second]);
            %date_GPS = utc2gpstow(date_GLO);
            
            %date_GLO = datenum([year month day hour minute second]);
            date_GPS = utc2gpstow([year month day hour minute second], 0);

            %convert GPS date to seconds of week (used as GLONASS time-of-ephemeris)
            [week_toe, toe] = date2gps(datevec(date_GPS));

            %save ephemerides (position, velocity and acceleration vectors in ECEF system PZ-90.02)
            Eph(1,i)  = svprn;
            Eph(2,i)  = TauN;
            Eph(3,i)  = GammaN;
            Eph(4,i)  = tk;
            Eph(5,i)  = X*1e3;  %satellite X coordinate at ephemeris reference time [m]
            Eph(6,i)  = Y*1e3;  %satellite Y coordinate at ephemeris reference time [m]
            Eph(7,i)  = Z*1e3;  %satellite Z coordinate at ephemeris reference time [m]
            Eph(8,i)  = Xv*1e3; %satellite velocity along X at ephemeris reference time [m/s]
            Eph(9,i)  = Yv*1e3; %satellite velocity along Y at ephemeris reference time [m/s]
            Eph(10,i) = Zv*1e3; %satellite velocity along Z at ephemeris reference time [m/s]
            Eph(11,i) = Xa*1e3; %acceleration due to lunar-solar gravitational perturbation along X at ephemeris reference time [m/s^2]
            Eph(12,i) = Ya*1e3; %acceleration due to lunar-solar gravitational perturbation along Y at ephemeris reference time [m/s^2]
            Eph(13,i) = Za*1e3; %acceleration due to lunar-solar gravitational perturbation along Z at ephemeris reference time [m/s^2]
            Eph(14,i) = E;
            Eph(15,i) = freq_num;
            Eph(16,i) = 0;
            Eph(17,i) = 0;
            Eph(18,i) = toe;
            Eph(19,i) = 0;
            Eph(20,i) = 0;
            Eph(21,i) = 0;
            Eph(22,i) = 0;
            Eph(23,i) = 0;
            Eph(24,i) = week_toe;
            Eph(25,i) = 0;
            Eph(26,i) = 0;
            Eph(27,i) = Bn; %health flag
            Eph(28,i) = 0;
            Eph(29,i) = 0;
            Eph(30,i) = (sys_index-1) + svprn;
            Eph(31,i) = int8(sys_id);
            Eph(32,i) = weektow2time(week_toe, toe, sys_id);
            Eph(33,i) = 0;
            Eph(34,i) = 0;            
            %
        end
        eph             =   Eph;
        
    end
    status = fclose(fide);

end
%
%%%%%%%%% end get_eph.m %%%%%%%%%