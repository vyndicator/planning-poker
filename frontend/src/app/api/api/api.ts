export * from './gitLabResource.service';
import { GitLabResourceService } from './gitLabResource.service';
export * from './sessionResource.service';
import { SessionResourceService } from './sessionResource.service';
export const APIS = [GitLabResourceService, SessionResourceService];
