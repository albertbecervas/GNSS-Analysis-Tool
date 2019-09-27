function [Obs_columns, nObs_types] = obs_type_find(Obs_types, sysId, sig)

% SYNTAX:
%   [Obs_columns, nObs_types] = obs_type_find(Obs_types, sysId);
%
% INPUT:
%   Obs_types = cell of strings containing observation types
%               RINEX v2.xx --> e.g. L1C1P1...
%               RINEX v3.xx --> e.g. C1CL1CD1C...
%   sysId = cell-array containing one-letter identifiers for constellations
%
% OUTPUT:
%   Obs_columns = structure containing the column number of each observation type
%                 in the following fields:
%                   .L1 = L1 column
%                   .L2 = L2 column
%                   .L5 = L5 column
%                   .C1 = C1 column
%                   .C2 = C1 column
%                   .P1 = P1 column
%                   .P2 = P2 column
%                   .S1 = S1 column
%                   .S2 = S2 column
%                   .D1 = D1 column
%                   .D2 = D2 column
%                 In the case of RINEX v3.xx, an additional field is added
%                 for specifying the constellation, e.g.:
%                   .G.L1 (GPS)
%                   .R.L1 (GLONASS)
%   nObs_types = number of available observation types
%
% DESCRIPTION:
%   Detection of the column index for phase observations (L1, L2), for
%   code observations (C1, P1, P2), SNR ratios (S1, S2) and Doppler
%   measurements (D1, D2).

    if (isempty(sysId)) %RINEX v2.xx

        nObs_types = length(Obs_types)/2;

        %search L1 column
        s1 = strfind(Obs_types, 'L1');
        s2 = strfind(Obs_types, 'LA');
        s = [s1 s2];
        col_L1 = (s+1)/2;

        %search L2 column
        s1 = strfind(Obs_types, 'L2');
        s2 = strfind(Obs_types, 'LC');
        s = [s1 s2];
        col_L2 = (s+1)/2;

        %search L5 column
        s1 = strfind(Obs_types, 'L5');
        col_L5 = (s1+1)/2;

        %search C1 column
        s1 = strfind(Obs_types, 'C1');
        s2 = strfind(Obs_types, 'CA');
        s = [s1 s2];
        col_C1 = (s+1)/2;

        %search C2 column
        s1 = strfind(Obs_types, 'C2');
        col_C2 = (s1+1)/2;

        %search C5 column
        s1 = strfind(Obs_types, 'C5');
        col_C5 = (s1+1)/2;

        %search P1 column
        s1 = strfind(Obs_types, 'P1');
        s2 = strfind(Obs_types, 'CA'); %QZSS does not use P1
        s = [s1 s2];
        col_P1 = (s+1)/2;

        %if RINEX v2.12 and GPS/GLONASS P1 observations are not available
        if (length(col_P1) ~= 2 && ~isempty(s2))
            %keep QZSS CA observations as C1
            col_P1 = [];
        end

        %search P2 column
        s1 = strfind(Obs_types, 'P2');
        s2 = strfind(Obs_types, 'CC');
        s = [s1 s2];
        col_P2 = (s+1)/2;

        %search S1 column
        s1 = strfind(Obs_types, 'S1');
        s2 = strfind(Obs_types, 'SA');
        s = [s1 s2];
        col_S1 = (s+1)/2;

        %search S2 column
        s1 = strfind(Obs_types, 'S2');
        s2 = strfind(Obs_types, 'SC');
        s = [s1 s2];
        col_S2 = (s+1)/2;

        %search D1 column
        s1 = strfind(Obs_types, 'D1');
        s2 = strfind(Obs_types, 'DA');
        s = [s1 s2];
        col_D1 = (s+1)/2;

        %search D2 column
        s1 = strfind(Obs_types, 'D2');
        s2 = strfind(Obs_types, 'DC');
        s = [s1 s2];
        col_D2 = (s+1)/2;

        Obs_columns.L1 = col_L1;
        Obs_columns.L2 = col_L2;
        Obs_columns.L5 = col_L5;
        Obs_columns.C1 = col_C1;
        Obs_columns.C2 = col_C2;
        Obs_columns.C5 = col_C5;
        Obs_columns.P1 = col_P1;
        Obs_columns.P2 = col_P2;
        Obs_columns.S1 = col_S1;
        Obs_columns.S2 = col_S2;
        Obs_columns.D1 = col_D1;
        Obs_columns.D2 = col_D2;

    else %RINEX v3.xx
        for c = 1 : length(sysId)

            nObs_types.(sysId{c}) = size(Obs_types.(sysId{c}),2)/3;

            switch sysId{c}
                case 'GPS' %GPS
                    %               L1              %
                    idC1 = {'C1C'};                 %
                    idP1 = {'C1W';'C1P'};           %
                    idL1 = {'L1C'};                 %
                    idS1 = {'S1C'};                 %
                    idD1 = {'D1C'};                 %
                    %--------------------------------
    %               %               L2              %
                    idC2 = {'C2C';'C2L'};                 %
                    idL2 = {'L2C';'L2L'};                 %
                    idS2 = {'S2C';'S2L'};                 %
                    idD2 = {'D2C';'D2L'};                 %
                    %--------------------------------
                    %               L5              %
                    idC5 = {'C5Q'};                 %
                    idL5 = {'L5Q'};                 %
                    idS5 = {'S5Q'};                 %
                    idD5 = {'D5Q'};                 %
                    %--------------------------------
                case 'GLO' %GLONASS
                    %               L1              %
                    idC1 = {'C1C'};                 %
                    idP1 = {'C1P'};                 %
                    idL1 = {'L1C'};                 %
                    idS1 = {'S1C'};                 %
                    idD1 = {'D1C'};                 %
                    %--------------------------------
                    %               L2              %
                    idC2 = {'C2C'};                 %
                    idL2 = {'L2C'};                 %
                    idS2 = {'S2C'};                 %
                    idD2 = {'D2C'};                 %
                case 'GAL' %Galileo
                    %               E1              %
                    idC1 = {'C1C'};                 %
                    idP1 = {'...'};                 %
                    idL1 = {'L1C'};                 %
                    idS1 = {'S1C'};                 %
                    idD1 = {'D1C'};                 %
                    %--------------------------------
                    %               E5a             %
                    idC5 = {'C5X';'C5Q'};           %
                    idL5 = {'L5X';'L5Q'};           %
                    idS5 = {'S5X';'S5Q'};           %
                    idD5 = {'D5X';'D5Q'};           %
                    %--------------------------------
    %                 %               E5b             %
    %                 idC7 = {'C7X';'C7Q'};           %
    %                 idL7 = {'L7X';'L7Q'};           %
    %                 idS7 = {'S7X';'S7Q'};           %
    %                 idD7 = {'D7X';'D7Q'};           %
                    %--------------------------------
                    %               E5              %
                    idC2 = {'C8X';'C8Q'};           %
                    idL2 = {'L8X';'L8Q'};           %
                    idS2 = {'S8X';'S8Q'};           %
                    idD2 = {'D8X';'D8Q'};           %
                case 'CMP' %Compass/Beidou
                    idC1 = {'C2X';'C2Q';'C2I'};   %B1
                    idP1 = {'...'};               %B1
                    idL1 = {'L2X';'L2Q';'L2I'};   %B1
                    idS1 = {'S2X';'S2Q';'S2I'};   %B1
                    idD1 = {'D2X';'D2Q';'D2I'};   %B1
                    %--------------------------------
                    idL2 = {'L7I';'L7Q'};         %B2
                    idC2 = {'C7I';'C7Q'};         %B2
                    idS2 = {'S7I';'S7Q'};         %B2
                    idD2 = {'D7I';'D7Q'};         %B2
                case 'J' %QZSS
                    idC1 = {'C1X';'C1C'};         %J1
                    idP1 = {'...'};               %J1
                    idL1 = {'L1X';'L1C'};         %J1
                    idS1 = {'S1X';'S1C'};         %J1
                    idD1 = {'D1X';'D1C'};         %J1
                    %--------------------------------
    %                 idP2 = {'C2X';'C2C'};         %J2
                    idL2 = {'L2X';'L2C'};         %J2
                    idS2 = {'S2X';'S2C'};         %J2
                    idD2 = {'D2X';'D2C'};         %J2
            end

            % METER ifs PARA CADA SE?AL !!! %
            if(~isempty(strcmp('L1',sig)))
                %search L1 column
                for i = 1 : length(idL1)
                    s = strfind(Obs_types.(sysId{c}), idL1{i}); if (~isempty(s)), break, end;
                end
                col_L1 = (s+2)/3;
                Obs_columns.(sysId{c}).L1 = col_L1;

                %search C1 column
                for i = 1 : length(idC1)
                    s = strfind(Obs_types.(sysId{c}), idC1{i}); if (~isempty(s)), break, end;
                end
                col_C1 = (s+2)/3;

                %search S1 column
                for i = 1 : length(idS1)
                    s = strfind(Obs_types.(sysId{c}), idS1{i}); if (~isempty(s)), break, end;
                end
                col_S1 = (s+2)/3;    

                %search D1 column
                for i = 1 : length(idD1)
                    s = strfind(Obs_types.(sysId{c}), idD1{i}); if (~isempty(s)), break, end;
                end
                col_D1 = (s+2)/3;
            end

            if(~isempty(strcmp('L2',sig)))
                %search L2 column
                for i = 1 : length(idL2)
                    s = strfind(Obs_types.(sysId{c}), idL2{i}); if (~isempty(s)), break, end;
                end
                col_L2 = (s+2)/3;
                Obs_columns.(sysId{c}).L2 = col_L2;

                %search C2 column
                for i = 1 : length(idC2)
                    s = strfind(Obs_types.(sysId{c}), idC2{i}); if (~isempty(s)), break, end;
                end
                col_C2 = (s+2)/3;

                %search S2 column
                for i = 1 : length(idS2)
                    s = strfind(Obs_types.(sysId{c}), idS2{i}); if (~isempty(s)), break, end;
                end
                col_S2 = (s+2)/3;

                %search D2 column
                for i = 1 : length(idD2)
                    s = strfind(Obs_types.(sysId{c}), idD2{i}); if (~isempty(s)), break, end;
                end
                col_D2 = (s+2)/3;        
            end

            if(~isempty(strcmp('L5',sig)) && ((strcmp(sysId{c},'GPS') == 1) || (strcmp(sysId{c},'GAL') == 1)))
                %search L5 column
                for i = 1 : length(idL5)
                    s = strfind(Obs_types.(sysId{c}), idL5{i}); if (~isempty(s)), break, end;
                end
                col_L5 = (s+2)/3;
                Obs_columns.(sysId{c}).L5 = col_L5;

                %search C5 column
                for i = 1 : length(idC5)
                    s = strfind(Obs_types.(sysId{c}), idC5{i}); if (~isempty(s)), break, end;
                end
                col_C5 = (s+2)/3;
                Obs_columns.(sysId{c}).C5 = col_C5;

                %search D5 column
                for i = 1 : length(idD5)
                    s = strfind(Obs_types.(sysId{c}), idD5{i}); if (~isempty(s)), break, end;
                end
                col_D5 = (s+2)/3;
                Obs_columns.(sysId{c}).D5 = col_D5;       
            end            

            Obs_columns.(sysId{c}).C1 = col_C1;
            Obs_columns.(sysId{c}).C2 = col_C2;

            Obs_columns.(sysId{c}).S1 = col_S1;
            Obs_columns.(sysId{c}).S2 = col_S2;

            Obs_columns.(sysId{c}).D1 = col_D1;
            Obs_columns.(sysId{c}).D2 = col_D2;
        end
    end
end

    
   