function    [Obs,svn]     =   getObs(fid,Nsvn,Nobs,vers,const,Obs_types)
% getObs:   Get the observations of the selected epoch in a RINEX file.
%           Reads observations of Nsvn satellites. The selected epoch is
%           given by the position of the fid. It controls the RINEX version
%           given by vers = {2,3} for RINEX 2.XX or 3.XX, resp. For version
%           3, the function also outputs the SVN (string) in the given
%           epoch.
%
% Modified from Kai Borre 09-13-96

    global lin

    
    % Get number ob observation lines for each epoch/svn
    if( vers == 2)  % RINEX 2.XX (only 5 types of observation per line)
        Obs     =   nan(Nsvn,Nobs);
        Nlin    =   ceil(Nobs/5);
        svn     =   [];
        [tmp,~] =   obs_type_find(Obs_types,{const},'L1');
        colc    =   tmp.C1; 
    else            % RINEX 3.XX (All observation in the same line/svn)
        Nlin    =   1;
        svn     =   [];
        Obs     =   [];
        switch const
            case 'GPS'
                strc    =   'G';            % Constellation letter for GPS
            case 'GLO'
                strc    =   'R';            % Constellation letter for GLONASS
            case 'GAL'
                strc    =   'E';            % Constellation letter for Galileo
        end
        [tmp,~]         =   obs_type_find(Obs_types,{const},'L1');
        colc            =   tmp.(const).C1;        
    end
    %
    %-  Read line for each satellite
    for ii = 1:Nsvn
        if( vers == 2 )
            for jj = 1:Nlin
                lin     =   fgetl(fid);     % Get line jj for SVN ii
                init    =   (jj-1)*5;
                if( jj == Nlin )
                    Ntmp    =   Nobs - init;
                else
                    Ntmp    =   5;
                end
                %-  Store each observable (Code Pseudorange) in the
                %   corresponding position of the satellite svn(ii)
                init        =   1+16*(colc-1);
                fin         =   16*colc-2;
                pr          =   str2num(lin(init:fin));
                if isempty(pr)
                    Obs     =   [Obs NaN];
                else
                    Obs     =   [Obs pr];
                end
            end        
        else
            lin             =   fgetl(fid);     % Get line for SVN ii
            %
            if( strcmp(lin(1),strc) )
                svn         =   [svn str2num(lin(2:3))];       % Check format RINEX 3.XX
                %-  Store each observable (Code Pseudorange) in the
                %   corresponding position of the satellite svn(ii)
                init        =   3+1+16*(colc-1);
                fin         =   16*colc-2+3;
                pr          =   str2num(lin(init:fin));
                if isempty(pr)
                    Obs     =   [Obs NaN];
                else
                    Obs     =   [Obs pr];
                end
            end
        end
    end

end