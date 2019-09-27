function    [I,ncomm]     =   getIonoCorrDualFreq(band1, band2, pr1, pr2)
% getProp_tcorr:  Get propagation effects correction.  
%
% Inputs:
%           - band1:    Frequency of the band 1     [Hz]
%           - band2:    Frequency of the band 2     [Hz]
%           - pr1:      Pseudoranges and SVID of the satellites of the band 1
%           - pr2:      Pseudoranges and SVID of the satellites of the band 2
% Outputs:

%           - I:        Ionosphere correction at band 2 using band 1
%                       measurements

%%

% if size(pr1, 1) > size(pr5, 1)
%     pr5c = zeros(1, size(pr1, 1));
% else
%     pr1c = zeros(1, size(pr5, 1));
% end

%% General algorithm, correction for band2
tmp     =   (band1^2)/( (band2^2) - (band1^2)); 

[comm, idx1, idx2]  =   intersect(pr1(1, :), pr2(1, :));

for i=1:length(comm)
   tmp1(i) = pr1(2, idx1(i)) - pr2(2, idx2(i));
end

tmp1 = [comm; tmp1];
[ncomm, idx3]       =   setdiff(pr2(1, :), tmp1(1, :));

for i=1:length(idx3)
    if idx3(i) == 1    
        tmp1 = [[pr2(1, idx3(i));0] tmp1];      
    else
        if idx3(i) == size(pr2, 2)   
            tmp1 = [tmp1 [pr2(1, idx3(i));0]];      
        else
            tmp1 = [tmp1(:, (1:idx3(i)-1)) [pr2(1, idx3(i));0] tmp1(:, idx3(i):end)];
        end
    end
end

I       =   tmp*tmp1(2, :);
    
 
end