"use client";

import { Dispatch, SetStateAction, FC, useState, useEffect } from "react";
import Modal from "@/components/shared/modal";
import Flag from "react-world-flags"; // 국기 아이콘 라이브러리

interface ProfileModalProps {
  showModal: boolean;
  setShowModal: Dispatch<SetStateAction<boolean>>;
}

interface UserCountryDto {
  id: number;
  email: string;
  countryId: number;
}

interface CountryResponse {
  id: number;
  continentName: string;
  countryEngName: string;
  countryIsoAlp2: string;
  countryName: string;
  flagDownloadUrl: string;
  mapDownloadUrl: string;
}

const ProfileModal: FC<ProfileModalProps> = ({ showModal, setShowModal }) => {
  const [profile, setProfile] = useState<{
    id: string;
    name: string;
    email: string;
  } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userCountries, setUserCountries] = useState<UserCountryDto[] | null>(
    null,
  );
  const [countries, setCountries] = useState<CountryResponse[]>([]);

  useEffect(() => {
    if (showModal) {
      const token = localStorage.getItem("accessToken");
      if (!token) {
        setError("로그인 정보가 없습니다.");
        setLoading(false);
        return;
      }

      // 사용자 프로필과 관심 국가 정보를 가져옵니다.
      Promise.all([
        fetch("http://localhost:8080/users/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }).then((response) => response.json()),
        fetch("http://localhost:8080/UserCountries", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }).then((response) => response.json()),
      ])
        .then(([profileData, userCountriesData]) => {
          setProfile(profileData);
          setUserCountries(userCountriesData);

          // 각 관심 국가의 정보를 가져옵니다.
          return Promise.all(
            userCountriesData.map((userCountry: UserCountryDto) =>
              fetch(`http://localhost:8080/country/${userCountry.countryId}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              }).then((response) => response.json()),
            ),
          );
        })
        .then((countriesData) => {
          setCountries(countriesData);
          setLoading(false);
        })
        .catch((err) => {
          console.error("데이터 조회 오류:", err);
          setError("데이터를 가져오는 중 오류가 발생했습니다.");
          setLoading(false);
        });
    }
  }, [showModal]);

  if (loading) {
    return (
      <Modal showModal={showModal} setShowModal={setShowModal}>
        <div className="p-8 text-center">
          <h2 className="mb-4 text-2xl font-bold text-gray-800">프로필</h2>
          <div className="mx-auto h-12 w-12 animate-spin rounded-full border-b-2 border-gray-900"></div>
        </div>
      </Modal>
    );
  }

  if (error) {
    return (
      <Modal showModal={showModal} setShowModal={setShowModal}>
        <div className="p-8">
          <h2 className="mb-4 text-2xl font-bold text-gray-800">프로필</h2>
          <p className="mb-4 text-red-500">{error}</p>
          <button
            onClick={() => setShowModal(false)}
            className="w-full rounded-lg bg-red-500 px-4 py-2 text-white transition-colors duration-300 hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
          >
            닫기
          </button>
        </div>
      </Modal>
    );
  }

  return (
    <Modal showModal={showModal} setShowModal={setShowModal}>
      <div className="p-6">
        <h2 className="mb-4 text-2xl font-bold text-gray-800">프로필</h2>
        {profile ? (
          <div className="space-y-4">
            <p className="text-gray-700">
              <strong className="text-gray-900">이름:</strong> {profile.name}
            </p>
            <p className="text-gray-700">
              <strong className="text-gray-900">이메일:</strong> {profile.email}
            </p>
            <div>
              <p className="mb-3 text-gray-700">
                <strong className="text-gray-900">관심국가:</strong>
              </p>
              {countries.length > 0 ? (
                <div className="flex flex-wrap gap-4">
                  {countries.map((country) => (
                    <div key={country.id} className="flex items-center">
                      <div className="inline-flex cursor-pointer items-center rounded-full bg-gray-100 px-3 py-1.5 transition-colors duration-200 hover:bg-gray-200">
                        <Flag
                          code={country.countryIsoAlp2}
                          className="h-4 w-6 rounded-sm"
                          alt={country.countryName}
                        />
                        <span className="ml-2 text-sm font-medium text-gray-700">
                          {country.countryName}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="italic text-gray-500">관심국가 없습니다.</p>
              )}
            </div>
          </div>
        ) : (
          <p>프로필 정보가 없습니다.</p>
        )}
        <button
          onClick={() => setShowModal(false)}
          className="mt-4 w-full rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600"
        >
          닫기
        </button>
      </div>
    </Modal>
  );
};

export default ProfileModal;
