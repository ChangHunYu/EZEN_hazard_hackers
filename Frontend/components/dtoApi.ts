// ItemDto 정의
export interface ItemDto {
  id: number;
  isChecked: boolean;
  isDeleted: boolean;
  checklistId: number;
  createdAt: string; // ISO 8601 날짜 문자열
  updatedAt: string; // ISO 8601 날짜 문자열
  description: string;
}

// ChecklistDto 정의
export interface ChecklistDto {
  id: number;
  userId: number;
  itineraryId: number;
  title: string;
  items: ItemDto[];
  deleted: boolean;
}

export interface countryDto {
  id: number;
  continentName: string;
  alertId: number;
  alertLevel: number;
  countryEngName: string;
  countryIsoAlp2: string;
  countryName: string;
  flagDownloadUrl: string;
  mapDownloadUrl: string;
}
