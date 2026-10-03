import {TaporiRepository} from '../../domain/repositories/TaporiRepository';
import {taporiRemoteDataSource} from '../remote/TaporiRemoteDataSource';

export const taporiRepository: TaporiRepository = taporiRemoteDataSource;
